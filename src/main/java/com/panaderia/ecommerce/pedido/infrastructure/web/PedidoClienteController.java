package com.panaderia.ecommerce.pedido.infrastructure.web;

import com.panaderia.ecommerce.cliente.application.ClienteService;
import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.shared.infrastructure.pdf.ComprobanteService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Checkout (crear pedido a partir del carrito) e historial "Mis Pedidos" del cliente.
 */
@Controller
@RequestMapping("/cliente")
public class PedidoClienteController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoClienteController.class);
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Lima");

    /** Estados en los que el pedido ya fue pagado y por lo tanto puede emitirse comprobante. */
    private static final java.util.Set<String> ESTADOS_PAGADOS = java.util.Set.of(
            "PAGADO", "EN_PREPARACION", "LISTO_PARA_RECOJO", "EN_CAMINO", "ENTREGADO");

    private final ClienteService clienteService;
    private final JdbcTemplate jdbcTemplate;
    private final ComprobanteService comprobanteService;

    public PedidoClienteController(ClienteService clienteService, JdbcTemplate jdbcTemplate,
                                    ComprobanteService comprobanteService) {
        this.clienteService = clienteService;
        this.jdbcTemplate = jdbcTemplate;
        this.comprobanteService = comprobanteService;
    }

    private Cliente clienteActual(Authentication authentication) {
        return clienteService.obtenerClientePorEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    private Long carritoIdDe(Long clienteId) {
        List<Long> ids = jdbcTemplate.query("SELECT id_carrito FROM carrito WHERE id_cliente = ?",
                new Object[]{clienteId}, (rs, rowNum) -> rs.getLong(1));
        return ids.isEmpty() ? null : ids.get(0);
    }

    // ---------- Checkout ----------

    @GetMapping("/checkout")
    public String checkoutForm(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        Cliente actual = clienteActual(authentication);
        Long carritoId = carritoIdDe(actual.getId());
        List<CarritoController.ItemCarritoDTO> items = carritoId == null ? List.of() : jdbcTemplate.query(
                "SELECT cp.id_producto, p.nombre, p.foto, p.precio, p.stock_minimo, p.compra_minima, cp.cantidad, (p.precio * cp.cantidad) AS subtotal " +
                        "FROM carrito_producto cp JOIN producto p ON cp.id_producto = p.id_producto " +
                        "WHERE cp.id_carrito = ? ORDER BY p.nombre",
                new Object[]{carritoId},
                (rs, rowNum) -> new CarritoController.ItemCarritoDTO(
                        rs.getLong("id_producto"), rs.getString("nombre"), rs.getString("foto"),
                        rs.getBigDecimal("precio"), rs.getInt("stock_minimo"), rs.getInt("compra_minima"), rs.getInt("cantidad"),
                        rs.getBigDecimal("subtotal")));

        if (items.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tu carrito está vacío. Agrega productos antes de continuar.");
            return "redirect:/productos";
        }

        List<CarritoController.ItemCarritoDTO> itemsBajoMinimo = items.stream()
                .filter(item -> item.cantidad() < item.compraMinima())
                .toList();
        if (!itemsBajoMinimo.isEmpty()) {
            String detalle = itemsBajoMinimo.stream()
                    .map(item -> item.nombre() + " (mínimo " + item.compraMinima() + " unidades)")
                    .reduce((a, b) -> a + ", " + b).orElse("");
            redirectAttributes.addFlashAttribute("error",
                    "Ajusta la cantidad en tu carrito antes de continuar: " + detalle);
            return "redirect:/cliente/carrito";
        }

        List<Map<String, Object>> direcciones = jdbcTemplate.queryForList(
                "SELECT d.id_direccion AS id, d.alias, d.calle, d.numero, COALESCE(dist.nombre, '') AS distrito, d.referencia " +
                        "FROM direccion d LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito WHERE d.id_cliente = ? " +
                        "ORDER BY d.id_direccion",
                actual.getId());

        BigDecimal total = items.stream().map(CarritoController.ItemCarritoDTO::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cliente", actual);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("fechaMinima", LocalDate.now(ZONA_HORARIA).plusDays(1).toString());
        return "cliente/checkout";
    }

    @PostMapping("/checkout")
    public RedirectView confirmarPedido(@RequestParam String tipo_entrega,
                                         @RequestParam(required = false) Long id_direccion,
                                         @RequestParam String fecha_entrega,
                                         @RequestParam(defaultValue = "BOLETA") String tipo_comprobante,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            Long carritoId = carritoIdDe(actual.getId());
            if (carritoId == null) {
                throw new IllegalArgumentException("Tu carrito está vacío");
            }

            List<CarritoController.ItemCarritoDTO> items = jdbcTemplate.query(
                    "SELECT cp.id_producto, p.nombre, p.foto, p.precio, p.stock_minimo, p.compra_minima, cp.cantidad, (p.precio * cp.cantidad) AS subtotal " +
                            "FROM carrito_producto cp JOIN producto p ON cp.id_producto = p.id_producto " +
                            "WHERE cp.id_carrito = ?",
                    new Object[]{carritoId},
                    (rs, rowNum) -> new CarritoController.ItemCarritoDTO(
                            rs.getLong("id_producto"), rs.getString("nombre"), rs.getString("foto"),
                            rs.getBigDecimal("precio"), rs.getInt("stock_minimo"), rs.getInt("compra_minima"), rs.getInt("cantidad"),
                            rs.getBigDecimal("subtotal")));

            if (items.isEmpty()) {
                throw new IllegalArgumentException("Tu carrito está vacío");
            }

            for (CarritoController.ItemCarritoDTO item : items) {
                if (item.cantidad() > item.stockDisponible()) {
                    throw new IllegalArgumentException("No hay stock suficiente de " + item.nombre());
                }
                if (item.cantidad() < item.compraMinima()) {
                    throw new IllegalArgumentException("La compra mínima de " + item.nombre() + " es " + item.compraMinima() + " unidades");
                }
            }

            LocalDate fechaMinimaPermitida = LocalDate.now(ZONA_HORARIA).plusDays(1);
            LocalDate fechaEntregaSolicitada;
            try {
                fechaEntregaSolicitada = LocalDate.parse(fecha_entrega);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Fecha de entrega inválida");
            }
            if (fechaEntregaSolicitada.isBefore(fechaMinimaPermitida)) {
                throw new IllegalArgumentException("La fecha de entrega debe ser a partir de mañana (" + fechaMinimaPermitida + ")");
            }

            Long idDireccionEntrega = null;
            String tipoEntregaDb;
            if ("DELIVERY".equalsIgnoreCase(tipo_entrega) || "DOMICILIO".equalsIgnoreCase(tipo_entrega)) {
                tipoEntregaDb = "DOMICILIO";
                if (id_direccion == null) {
                    throw new IllegalArgumentException("Debes seleccionar una dirección para el delivery");
                }
                List<Long> ids = jdbcTemplate.query(
                        "SELECT id_direccion FROM direccion WHERE id_direccion = ? AND id_cliente = ?",
                        new Object[]{id_direccion, actual.getId()}, (rs, rowNum) -> rs.getLong(1));
                if (ids.isEmpty()) {
                    throw new IllegalArgumentException("Dirección inválida");
                }
                idDireccionEntrega = id_direccion;
            } else {
                tipoEntregaDb = "RECOJO_TIENDA";
            }

            BigDecimal totalBruto = items.stream().map(CarritoController.ItemCarritoDTO::subtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costoEnvio = BigDecimal.ZERO;
            BigDecimal descuento = BigDecimal.ZERO;
            BigDecimal subtotalSinIgv = totalBruto.divide(BigDecimal.ONE.add(IGV_RATE), 2, RoundingMode.HALF_UP);
            BigDecimal costoTotal = totalBruto.add(costoEnvio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);

            Long idDireccionFinal = idDireccionEntrega;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO pedido (id_cliente, id_sede, id_direccion_entrega, tipo_entrega, fecha_entrega, subtotal_productos, costo_envio, descuento, costo_total, estado, tipo_comprobante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, actual.getId());
                ps.setNull(2, java.sql.Types.INTEGER);
                if (idDireccionFinal != null) {
                    ps.setLong(3, idDireccionFinal);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, tipoEntregaDb);
                ps.setDate(5, Date.valueOf(fecha_entrega));
                ps.setBigDecimal(6, subtotalSinIgv);
                ps.setBigDecimal(7, costoEnvio);
                ps.setBigDecimal(8, descuento);
                ps.setBigDecimal(9, costoTotal);
                ps.setString(10, "PENDIENTE_PAGO");
                ps.setString(11, tipo_comprobante);
                return ps;
            }, keyHolder);

            Number pedidoIdNumber = keyHolder.getKey();
            if (pedidoIdNumber == null) {
                throw new IllegalStateException("No se pudo crear el pedido");
            }
            long pedidoId = pedidoIdNumber.longValue();

            for (CarritoController.ItemCarritoDTO item : items) {
                jdbcTemplate.update(
                        "INSERT INTO pedido_producto (id_pedido, id_producto, cantidad, precio_unitario_congelado, subtotal) VALUES (?, ?, ?, ?, ?)",
                        pedidoId, item.productoId(), item.cantidad(), item.precioUnitario(), item.subtotal());
                jdbcTemplate.update("UPDATE producto SET stock_minimo = GREATEST(stock_minimo - ?, 0) WHERE id_producto = ?",
                        item.cantidad(), item.productoId());
            }

            jdbcTemplate.update("DELETE FROM carrito_producto WHERE id_carrito = ?", carritoId);

            redirectAttributes.addFlashAttribute("success", "¡Pedido #" + pedidoId + " realizado con éxito!");
            RedirectView redirectView = new RedirectView("/cliente/pedidos/" + pedidoId, true);
            redirectView.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
            return redirectView;
        } catch (Exception e) {
            logger.error("Error creando pedido de cliente", e);
            redirectAttributes.addFlashAttribute("error", "No se pudo confirmar el pedido: " + e.getMessage());
            RedirectView redirectView = new RedirectView("/cliente/checkout", true);
            redirectView.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
            return redirectView;
        }
    }

    // ---------- Mis pedidos ----------

    @GetMapping("/pedidos")
    public String misPedidos(Model model, Authentication authentication) {
        Cliente actual = clienteActual(authentication);
        List<PedidoResumen> pedidos = jdbcTemplate.query(
                "SELECT id_pedido, estado, fecha_registro, fecha_entrega, tipo_entrega, costo_total " +
                        "FROM pedido WHERE id_cliente = ? ORDER BY fecha_registro DESC",
                new Object[]{actual.getId()},
                (rs, rowNum) -> new PedidoResumen(
                        rs.getLong("id_pedido"),
                        rs.getString("estado"),
                        Optional.ofNullable(rs.getTimestamp("fecha_registro")).map(ts -> ts.toLocalDateTime().toString()).orElse("-"),
                        Optional.ofNullable(rs.getDate("fecha_entrega")).map(Date::toString).orElse("-"),
                        rs.getString("tipo_entrega"),
                        rs.getBigDecimal("costo_total")));
        model.addAttribute("pedidos", pedidos);
        return "cliente/pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String detallePedido(@PathVariable("id") Long pedidoId, Model model, Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Cliente actual = clienteActual(authentication);
        Optional<PedidoDetalle> detalle = loadDetalle(pedidoId, actual.getId());
        if (detalle.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/cliente/pedidos";
        }
        model.addAttribute("pedido", detalle.get());
        model.addAttribute("detalles", loadDetalleItems(pedidoId));
        model.addAttribute("pagado", ESTADOS_PAGADOS.contains(detalle.get().estado()));
        return "cliente/detalle-pedido";
    }

    @GetMapping("/pedidos/{id}/comprobante")
    public void descargarComprobante(@PathVariable("id") Long pedidoId, Authentication authentication,
                                      HttpServletResponse response) {
        try {
            Cliente actual = clienteActual(authentication);
            List<Map<String, Object>> filas = jdbcTemplate.queryForList(
                    "SELECT tipo_comprobante, estado FROM pedido WHERE id_pedido = ? AND id_cliente = ?",
                    pedidoId, actual.getId());
            if (filas.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Map<String, Object> fila = filas.get(0);
            String estado = (String) fila.get("estado");
            if (!ESTADOS_PAGADOS.contains(estado)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("El comprobante estará disponible una vez que el pedido esté pagado.");
                return;
            }
            String tipoComprobante = fila.get("tipo_comprobante") != null ? (String) fila.get("tipo_comprobante") : "comprobante";

            byte[] pdfBytes = comprobanteService.generarComprobante(pedidoId, tipoComprobante);
            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            String filename = tipoComprobante.toLowerCase() + "_pedido_" + pedidoId + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength(pdfBytes.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfBytes);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("Error descargando comprobante del cliente", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<PedidoDetalle> loadDetalle(Long pedidoId, Long clienteId) {
        try {
            return jdbcTemplate.query(
                    "SELECT p.id_pedido, p.fecha_registro, p.fecha_entrega, p.tipo_entrega, p.estado, " +
                            "p.subtotal_productos, p.costo_envio, p.costo_total, p.tipo_comprobante, " +
                            "d.calle, d.numero, d.referencia, dist.nombre AS distrito_nombre " +
                            "FROM pedido p LEFT JOIN direccion d ON p.id_direccion_entrega = d.id_direccion " +
                            "LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito " +
                            "WHERE p.id_pedido = ? AND p.id_cliente = ?",
                    new Object[]{pedidoId, clienteId}, rs -> {
                        if (rs.next()) {
                            return Optional.of(new PedidoDetalle(
                                    rs.getLong("id_pedido"),
                                    Optional.ofNullable(rs.getTimestamp("fecha_registro")).map(ts -> ts.toLocalDateTime().toString()).orElse("-"),
                                    Optional.ofNullable(rs.getDate("fecha_entrega")).map(Date::toString).orElse("-"),
                                    rs.getString("tipo_entrega"),
                                    rs.getString("estado"),
                                    rs.getBigDecimal("subtotal_productos"),
                                    rs.getBigDecimal("costo_envio"),
                                    rs.getBigDecimal("costo_total"),
                                    rs.getString("tipo_comprobante"),
                                    rs.getString("calle"),
                                    rs.getString("numero"),
                                    rs.getString("referencia"),
                                    rs.getString("distrito_nombre")
                            ));
                        }
                        return Optional.empty();
                    });
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    private List<PedidoDetalleItem> loadDetalleItems(Long pedidoId) {
        try {
            return jdbcTemplate.query(
                    "SELECT pp.id_producto, p.nombre, p.foto, pp.cantidad, pp.precio_unitario_congelado, pp.subtotal " +
                            "FROM pedido_producto pp JOIN producto p ON pp.id_producto = p.id_producto WHERE pp.id_pedido = ?",
                    new Object[]{pedidoId},
                    (rs, rowNum) -> new PedidoDetalleItem(
                            rs.getLong("id_producto"), rs.getString("nombre"), rs.getString("foto"),
                            rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario_congelado"), rs.getBigDecimal("subtotal")));
        } catch (DataAccessException ex) {
            return List.of();
        }
    }

    public record PedidoResumen(Long id, String estado, String fechaRegistro, String fechaEntrega, String tipoEntrega, BigDecimal total) {
    }

    public record PedidoDetalle(Long id, String fechaRegistro, String fechaEntrega, String tipoEntrega, String estado,
                                 BigDecimal subtotalProductos, BigDecimal costoEnvio, BigDecimal costoTotal,
                                 String tipoComprobante, String calle, String numero, String referencia, String distritoNombre) {
    }

    public record PedidoDetalleItem(Long productoId, String nombre, String foto, Integer cantidad,
                                     BigDecimal precioUnitario, BigDecimal subtotal) {
    }
}
