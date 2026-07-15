package com.panaderia.ecommerce.pedido.infrastructure.jdbc;

import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.cliente.domain.Direccion;
import com.panaderia.ecommerce.pedido.domain.CostoEnvio;
import com.panaderia.ecommerce.pedido.domain.EstadoPedido;
import com.panaderia.ecommerce.pedido.domain.FechaEntrega;
import com.panaderia.ecommerce.pedido.domain.ItemPedido;
import com.panaderia.ecommerce.pedido.domain.Pedido;
import com.panaderia.ecommerce.pedido.domain.PedidoRepository;
import com.panaderia.ecommerce.pedido.domain.TipoEntrega;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador de infraestructura (puerto de salida) para {@link PedidoRepository}.
 * <p>
 * Nota de arquitectura: la tabla `pedido` usa un ENUM de estado y tipo de entrega
 * más granular (PENDIENTE_PAGO, PAGADO, EN_CAMINO, LISTO_PARA_RECOJO, DOMICILIO,
 * RECOJO_TIENDA...) que el modelo de dominio {@link EstadoPedido}/{@link TipoEntrega}.
 * Este adaptador traduce explícitamente entre ambos mundos (ver {@link #estadoDbADominio}
 * y {@link #estadoDominioADb}), documentando el mapeo en vez de forzar los enums a coincidir.
 */
@Component
public class PedidoRepositoryJdbcAdapter implements PedidoRepository {

    private static final String SELECT_BASE =
            "SELECT p.id_pedido, p.id_cliente, p.tipo_entrega, p.estado, p.fecha_registro, " +
                    "p.fecha_entrega, p.costo_envio, " +
                    "d.alias, d.calle, d.numero, d.referencia, dist.nombre AS distrito_nombre " +
                    "FROM pedido p " +
                    "LEFT JOIN direccion d ON p.id_direccion_entrega = d.id_direccion " +
                    "LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito ";

    private final JdbcTemplate jdbcTemplate;

    public PedidoRepositoryJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        List<Map<String, Object>> filas = jdbcTemplate.queryForList(SELECT_BASE + "WHERE p.id_pedido = ?", id);
        if (filas.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pedidoDesdeFila(filas.get(0)));
    }

    @Override
    public List<Pedido> findAll() {
        return mapearFilas(jdbcTemplate.queryForList(SELECT_BASE + "ORDER BY p.id_pedido DESC"));
    }

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        return mapearFilas(jdbcTemplate.queryForList(
                SELECT_BASE + "WHERE p.id_cliente = ? ORDER BY p.id_pedido DESC", clienteId));
    }

    @Override
    public List<Pedido> findByEstado(EstadoPedido estado) {
        return mapearFilas(jdbcTemplate.queryForList(
                SELECT_BASE + "WHERE p.estado = ? ORDER BY p.id_pedido DESC", estadoDominioADb(estado)));
    }

    @Override
    public List<Pedido> findByFechaEntrega(LocalDate fecha) {
        return mapearFilas(jdbcTemplate.queryForList(
                SELECT_BASE + "WHERE p.fecha_entrega = ? ORDER BY p.id_pedido DESC", java.sql.Date.valueOf(fecha)));
    }

    @Override
    public Pedido save(Pedido pedido) {
        BigDecimal costoEnvio = pedido.getCostoEnvio() != null ? pedido.getCostoEnvio().getValor() : BigDecimal.ZERO;
        java.sql.Date fechaEntregaSql = pedido.getFechaEntrega() != null
                ? java.sql.Date.valueOf(pedido.getFechaEntrega().getFecha()) : null;

        if (pedido.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO pedido (id_cliente, tipo_entrega, fecha_entrega, costo_envio, " +
                                "subtotal_productos, costo_total, estado, tipo_comprobante) VALUES (?, ?, ?, ?, ?, ?, ?, 'BOLETA')",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, pedido.getClienteId());
                ps.setString(2, tipoEntregaDominioADb(pedido.getTipoEntrega()));
                if (fechaEntregaSql != null) {
                    ps.setDate(3, fechaEntregaSql);
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                ps.setBigDecimal(4, costoEnvio);
                ps.setBigDecimal(5, pedido.getSubtotal());
                ps.setBigDecimal(6, pedido.getTotal());
                ps.setString(7, estadoDominioADb(pedido.getEstado()));
                return ps;
            }, keyHolder);
            long nuevoId = keyHolder.getKey().longValue();
            guardarItems(nuevoId, pedido.getItems());
            return findById(nuevoId).orElseThrow(() -> new IllegalStateException("No se pudo recuperar el pedido guardado"));
        }

        jdbcTemplate.update(
                "UPDATE pedido SET tipo_entrega = ?, fecha_entrega = ?, costo_envio = ?, " +
                        "subtotal_productos = ?, costo_total = ?, estado = ? WHERE id_pedido = ?",
                tipoEntregaDominioADb(pedido.getTipoEntrega()), fechaEntregaSql, costoEnvio,
                pedido.getSubtotal(), pedido.getTotal(), estadoDominioADb(pedido.getEstado()), pedido.getId());
        jdbcTemplate.update("DELETE FROM pedido_producto WHERE id_pedido = ?", pedido.getId());
        guardarItems(pedido.getId(), pedido.getItems());
        return findById(pedido.getId()).orElseThrow(() -> new IllegalStateException("Pedido no encontrado tras actualizar"));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM pedido WHERE id_pedido = ?", id);
    }

    // ---------- Helpers de mapeo ----------

    private void guardarItems(Long pedidoId, List<ItemPedido> items) {
        for (ItemPedido item : items) {
            jdbcTemplate.update(
                    "INSERT INTO pedido_producto (id_pedido, id_producto, cantidad, precio_unitario_congelado, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    pedidoId, item.getProductoId(), item.getCantidad(),
                    item.getPrecioUnitario().getValor(), item.getSubtotal());
        }
    }

    private List<Pedido> mapearFilas(List<Map<String, Object>> filas) {
        List<Pedido> pedidos = new ArrayList<>();
        for (Map<String, Object> fila : filas) {
            pedidos.add(pedidoDesdeFila(fila));
        }
        return pedidos;
    }

    private Pedido pedidoDesdeFila(Map<String, Object> fila) {
        Long id = ((Number) fila.get("id_pedido")).longValue();
        Long clienteId = ((Number) fila.get("id_cliente")).longValue();

        Direccion direccion = null;
        Object calle = fila.get("calle");
        if (calle != null) {
            direccion = new Direccion(
                    (String) fila.get("alias"),
                    (String) calle,
                    (String) fila.get("numero"),
                    (String) fila.get("distrito_nombre"),
                    (String) fila.get("referencia"));
        }

        BigDecimal costoEnvioValor = fila.get("costo_envio") != null
                ? (BigDecimal) fila.get("costo_envio") : BigDecimal.ZERO;
        CostoEnvio costoEnvio = new CostoEnvio(costoEnvioValor, Currency.getInstance("PEN"));

        java.sql.Date fechaEntregaSql = (java.sql.Date) fila.get("fecha_entrega");
        FechaEntrega fechaEntrega = fechaEntregaSql != null ? new FechaEntrega(fechaEntregaSql.toLocalDate()) : null;

        java.sql.Timestamp fechaRegistro = (java.sql.Timestamp) fila.get("fecha_registro");
        LocalDateTime fechaRegistroDt = fechaRegistro != null ? fechaRegistro.toLocalDateTime() : LocalDateTime.now();

        List<ItemPedido> items = jdbcTemplate.query(
                "SELECT pp.id_producto, p.nombre, pp.cantidad, pp.precio_unitario_congelado " +
                        "FROM pedido_producto pp JOIN producto p ON pp.id_producto = p.id_producto WHERE pp.id_pedido = ?",
                new Object[]{id},
                (rs, rowNum) -> new ItemPedido(
                        rs.getLong("id_producto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        new Precio(rs.getBigDecimal("precio_unitario_congelado"), Currency.getInstance("PEN"))));

        return new Pedido(id, clienteId, items,
                tipoEntregaDbADominio((String) fila.get("tipo_entrega")),
                direccion, costoEnvio, fechaEntrega,
                estadoDbADominio((String) fila.get("estado")),
                fechaRegistroDt);
    }

    /** DB -&gt; dominio. EN_CAMINO no tiene equivalente exacto; se mapea al estado "en preparación avanzada" (LISTO). */
    private EstadoPedido estadoDbADominio(String estadoDb) {
        if (estadoDb == null) return EstadoPedido.PENDIENTE;
        return switch (estadoDb) {
            case "PENDIENTE_PAGO" -> EstadoPedido.PENDIENTE;
            case "PAGADO" -> EstadoPedido.CONFIRMADO;
            case "EN_PREPARACION" -> EstadoPedido.EN_PREPARACION;
            case "LISTO_PARA_RECOJO", "EN_CAMINO" -> EstadoPedido.LISTO;
            case "ENTREGADO" -> EstadoPedido.ENTREGADO;
            case "CANCELADO" -> EstadoPedido.CANCELADO;
            default -> EstadoPedido.PENDIENTE;
        };
    }

    private String estadoDominioADb(EstadoPedido estado) {
        if (estado == null) return "PENDIENTE_PAGO";
        return switch (estado) {
            case PENDIENTE -> "PENDIENTE_PAGO";
            case CONFIRMADO -> "PAGADO";
            case EN_PREPARACION -> "EN_PREPARACION";
            case LISTO -> "LISTO_PARA_RECOJO";
            case ENTREGADO -> "ENTREGADO";
            case CANCELADO -> "CANCELADO";
        };
    }

    private TipoEntrega tipoEntregaDbADominio(String tipoDb) {
        if (tipoDb == null) return TipoEntrega.RECOJO_EN_TIENDA;
        return "DOMICILIO".equals(tipoDb) ? TipoEntrega.DELIVERY : TipoEntrega.RECOJO_EN_TIENDA;
    }

    private String tipoEntregaDominioADb(TipoEntrega tipo) {
        return tipo == TipoEntrega.DELIVERY ? "DOMICILIO" : "RECOJO_TIENDA";
    }
}