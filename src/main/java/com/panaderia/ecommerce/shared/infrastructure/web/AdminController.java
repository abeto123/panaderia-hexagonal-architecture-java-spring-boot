package com.panaderia.ecommerce.shared.infrastructure.web;

import com.panaderia.ecommerce.catalog.application.ProductoService;
import com.panaderia.ecommerce.cliente.application.ClienteService;
import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.cliente.domain.Ruc;
import com.panaderia.ecommerce.cliente.domain.RazonSocial;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final JdbcTemplate jdbcTemplate;

    public AdminController(ClienteService clienteService,
                           ProductoService productoService,
                           JdbcTemplate jdbcTemplate) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String dashboard(Model model) {
        int totalClientes = clienteService.listarClientes().size();
        int totalProductos = productoService.listarProductos().size();
        int totalPedidos = queryCount("SELECT COUNT(*) FROM pedido");
        int pedidosEnProceso = queryCount("SELECT COUNT(*) FROM pedido WHERE estado IN ('PENDIENTE_PAGO', 'PAGADO', 'EN_PREPARACION', 'EN_CAMINO')");
        int lowStockCount = queryCount("SELECT COUNT(*) FROM producto WHERE stock_minimo <= 0");

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("pedidosEnProceso", pedidosEnProceso);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("recentOrders", loadRecentOrders());
        model.addAttribute("lowStockProducts", loadLowStockProducts());

        return "admin/dashboard";
    }

    @GetMapping("/gestionClientes")
    public String gestionClientes() {
        return "redirect:/admin/clientes";
    }

    @GetMapping("/gestionPedidos")
    public String gestionPedidos() {
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/hojaProduccion")
    public String hojaProduccionRedirect() {
        return "redirect:/admin/produccion";
    }

    @GetMapping("/nuevoPedido")
    public String nuevoPedidoForm(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("productos", productoService.listarProductos());
        return "admin/nuevo-pedido";
    }

    @GetMapping("/apiDirecciones/{clienteId}")
    @ResponseBody
    public List<DireccionDTO> obtenerDireccionesCliente(@PathVariable Long clienteId) {
        List<DireccionDTO> direcciones = jdbcTemplate.query(
            "SELECT d.id_direccion, d.alias, d.calle, d.numero, COALESCE(dist.nombre, '') AS distrito, d.referencia " +
                "FROM direccion d LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito WHERE d.id_cliente = ?",
            new Object[]{clienteId},
            (rs, rowNum) -> new DireccionDTO(
                rs.getLong("id_direccion"),
                rs.getString("alias"),
                rs.getString("calle"),
                rs.getString("numero"),
                rs.getString("distrito"),
                rs.getString("referencia")
            )
        );
        return direcciones;
    }

    @PostMapping("/registrarPedido")
    public RedirectView registrarPedido(@RequestParam Long id_cliente,
                                  @RequestParam String tipo_comprobante,
                                  @RequestParam String tipo_entrega,
                                  @RequestParam(required = false) String ventana_entrega,
                                  @RequestParam String fecha_entrega,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.obtenerCliente(id_cliente)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            if ("RECOJO_TIENDA".equals(tipo_entrega)) {
                ventana_entrega = "05:00-12:00";
            }

            Map<Long, Integer> cantidades = extractCantidades(request);
            if (cantidades.values().stream().allMatch(q -> q <= 0)) {
                throw new IllegalArgumentException("Debe seleccionar al menos un producto");
            }

            Map<Long, com.panaderia.ecommerce.catalog.domain.Producto> productosPorId = productoService.listarProductos().stream()
                    .collect(HashMap::new, (map, p) -> map.put(p.getId(), p), Map::putAll);

            BigDecimal totalBruto = BigDecimal.ZERO;
            List<OrderItem> items = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
                Long productoId = entry.getKey();
                Integer cantidad = entry.getValue();
                if (cantidad == null || cantidad <= 0) {
                    continue;
                }
                com.panaderia.ecommerce.catalog.domain.Producto producto = productosPorId.get(productoId);
                if (producto == null) {
                    continue;
                }
                int minimoCompra = producto.getStock().getCantidad();
                if (minimoCompra > 0 && cantidad < minimoCompra) {
                    throw new IllegalArgumentException("La cantidad mínima para " + producto.getNombre() + " es " + minimoCompra + ".");
                }
                BigDecimal precioUnitario = producto.getPrecio().getValor();
                BigDecimal subtotalProducto = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
                totalBruto = totalBruto.add(subtotalProducto);
                items.add(new OrderItem(productoId, producto.getNombre(), cantidad, precioUnitario, subtotalProducto));
            }

            Long idDireccionEntrega = null;
            if ("DOMICILIO".equals(tipo_entrega)) {
                String idDireccionString = request.getParameter("id_direccion");
                if (idDireccionString == null || idDireccionString.isBlank()) {
                    throw new IllegalArgumentException("Debe seleccionar una dirección para el delivery");
                }
                Long direccionId = Long.parseLong(idDireccionString);
                List<Long> ids = jdbcTemplate.query(
                        "SELECT id_direccion FROM direccion WHERE id_direccion = ? AND id_cliente = ?",
                        new Object[]{direccionId, id_cliente},
                        (rs, rowNum) -> rs.getLong("id_direccion")
                );
                if (ids.isEmpty()) {
                    throw new IllegalArgumentException("Dirección de delivery inválida");
                }
                idDireccionEntrega = direccionId;
            }
            Long idDireccionEntregaFinal = idDireccionEntrega;

            BigDecimal costoEnvio = BigDecimal.ZERO;
            BigDecimal descuento = BigDecimal.ZERO;
            BigDecimal totalBrutoFinal = totalBruto.setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotalFinal = totalBrutoFinal.divide(BigDecimal.ONE.add(IGV_RATE), 2, RoundingMode.HALF_UP);
            BigDecimal igv = totalBrutoFinal.subtract(subtotalFinal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costoTotal = totalBrutoFinal.add(costoEnvio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);

            BigDecimal costoEnvioFinal = costoEnvio;
            BigDecimal descuentoFinal = descuento;
            BigDecimal costoTotalFinal = costoTotal;

            KeyHolder pedidoKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO pedido (id_cliente, id_sede, id_direccion_entrega, tipo_entrega, fecha_entrega, subtotal_productos, costo_envio, descuento, costo_total, estado, tipo_comprobante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, cliente.getId());
                ps.setNull(2, java.sql.Types.INTEGER);
                if (idDireccionEntregaFinal != null) {
                    ps.setLong(3, idDireccionEntregaFinal);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, tipo_entrega);
                ps.setDate(5, Date.valueOf(fecha_entrega));
                ps.setBigDecimal(6, subtotalFinal);
                ps.setBigDecimal(7, costoEnvioFinal);
                ps.setBigDecimal(8, descuentoFinal);
                ps.setBigDecimal(9, costoTotalFinal);
                ps.setString(10, "PENDIENTE_PAGO");
                ps.setString(11, tipo_comprobante);
                return ps;
            }, pedidoKeyHolder);

            Number pedidoIdNumber = pedidoKeyHolder.getKey();
            if (pedidoIdNumber == null) {
                throw new IllegalStateException("No se pudo crear el pedido");
            }
            Long pedidoId = pedidoIdNumber.longValue();

            for (OrderItem item : items) {
                jdbcTemplate.update("INSERT INTO pedido_producto (id_pedido, id_producto, cantidad, precio_unitario_congelado, subtotal) VALUES (?, ?, ?, ?, ?)",
                        pedidoId, item.productoId(), item.cantidad(), item.precioUnitario(), item.subtotal());
            }

            redirectAttributes.addFlashAttribute("success", "Pedido creado correctamente");
        } catch (Exception e) {
            logger.error("Error creando pedido", e);
            redirectAttributes.addFlashAttribute("error", "No se pudo crear el pedido: " + e.getMessage());
        }
        RedirectView redirectView = new RedirectView("/admin/pedidos", true);
        redirectView.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
        return redirectView;
    }

    private Map<Long, Integer> extractCantidades(HttpServletRequest request) {
        Map<Long, Integer> cantidades = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String name = entry.getKey();
            if (name.startsWith("cantidades[")) {
                String idString = name.substring(name.indexOf('[') + 1, name.indexOf(']'));
                try {
                    Long productoId = Long.parseLong(idString);
                    int cantidad = Integer.parseInt(entry.getValue()[0]);
                    cantidades.put(productoId, cantidad);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return cantidades;
    }

    public record DireccionDTO(Long idDireccion, String alias, String calle, String numero, String distrito, String referencia) {
    }

    public record OrderItem(Long productoId, String nombreProducto, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminarCliente(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "admin/clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", null);
        return "admin/cliente-form";
    }

    @PostMapping("/clientes")
    public String crearCliente(@RequestParam String nombres,
                               @RequestParam String apellidos,
                               @RequestParam String email,
                               @RequestParam(required = false) String telefono,
                               @RequestParam(required = false) String ruc,
                               @RequestParam(required = false) String razonSocial,
                               @RequestParam(required = false) String rol,
                               RedirectAttributes redirectAttributes) {
        try {
            com.panaderia.ecommerce.autenticacion.domain.Rol rolEnum = null;
            if (rol != null && !rol.isBlank()) {
                rolEnum = com.panaderia.ecommerce.autenticacion.domain.Rol.valueOf(rol);
            }
                clienteService.crearCliente(nombres, apellidos, email, telefono,
                    ruc != null && !ruc.isBlank() ? new Ruc(ruc) : null,
                    razonSocial != null && !razonSocial.isBlank() ? new RazonSocial(razonSocial) : null,
                    rolEnum);
            redirectAttributes.addFlashAttribute("success", "Cliente creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear el cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerCliente(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        model.addAttribute("cliente", cliente);
        return "admin/cliente-form";
    }

    @GetMapping("/clientes/{id}/direcciones")
    public String listarDireccionesCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerCliente(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        List<Map<String, Object>> direcciones = jdbcTemplate.queryForList(
            "SELECT d.id_direccion AS id, d.alias, d.calle, d.numero, COALESCE(dist.nombre, '') AS distrito, d.referencia " +
                "FROM direccion d LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito WHERE d.id_cliente = ?",
            id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("direcciones", direcciones);
        return "admin/cliente-direcciones";
    }

    @PostMapping("/clientes/{id}/direcciones")
    public String agregarDireccionCliente(@PathVariable Long id,
                                          @RequestParam(required = false) String alias,
                                          @RequestParam String calle,
                                          @RequestParam(required = false) String numero,
                                          @RequestParam(required = false) Long id_distrito,
                                          @RequestParam(required = false) String referencia,
                                          RedirectAttributes redirectAttributes) {
        try {
            if (id_distrito != null) {
                jdbcTemplate.update("INSERT INTO direccion (calle, numero, referencia, alias, id_cliente, id_distrito) VALUES (?, ?, ?, ?, ?, ?)",
                        calle, numero, referencia, alias, id, id_distrito);
            } else {
                jdbcTemplate.update("INSERT INTO direccion (calle, numero, referencia, alias, id_cliente, id_distrito) VALUES (?, ?, ?, ?, ?, NULL)",
                        calle, numero, referencia, alias, id);
            }
            redirectAttributes.addFlashAttribute("success", "Dirección agregada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar la dirección: " + e.getMessage());
        }
        return "redirect:/admin/clientes/" + id + "/direcciones";
    }

    @PostMapping("/clientes/{id}/direcciones/{dirId}/eliminar")
    public String eliminarDireccionCliente(@PathVariable Long id, @PathVariable Long dirId, RedirectAttributes redirectAttributes) {
        try {
            jdbcTemplate.update("DELETE FROM direccion WHERE id_direccion = ? AND id_cliente = ?", dirId, id);
            redirectAttributes.addFlashAttribute("success", "Dirección eliminada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la dirección: " + e.getMessage());
        }
        return "redirect:/admin/clientes/" + id + "/direcciones";
    }

    @GetMapping("/apiDepartamentos")
    @ResponseBody
    public List<Map<String, Object>> apiDepartamentos() {
        return jdbcTemplate.queryForList("SELECT id_departamento AS id, nombre FROM departamento ORDER BY nombre");
    }

    @GetMapping("/apiProvincias/{departamentoId}")
    @ResponseBody
    public List<Map<String, Object>> apiProvincias(@PathVariable Long departamentoId) {
        return jdbcTemplate.queryForList("SELECT id_provincia AS id, nombre FROM provincia WHERE id_departamento = ? ORDER BY nombre", departamentoId);
    }

    @GetMapping("/apiDistritos/{provinciaId}")
    @ResponseBody
    public List<Map<String, Object>> apiDistritos(@PathVariable Long provinciaId) {
        return jdbcTemplate.queryForList("SELECT id_distrito AS id, nombre FROM distrito WHERE id_provincia = ? ORDER BY nombre", provinciaId);
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @RequestParam String nombres,
                                    @RequestParam String apellidos,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String telefono,
                                    @RequestParam(required = false) String ruc,
                                    @RequestParam(required = false) String razonSocial,
                                    @RequestParam(required = false) String rol,
                                    RedirectAttributes redirectAttributes) {
        try {
            com.panaderia.ecommerce.autenticacion.domain.Rol rolEnum = null;
            if (rol != null && !rol.isBlank()) {
                rolEnum = com.panaderia.ecommerce.autenticacion.domain.Rol.valueOf(rol);
            }
                clienteService.editarCliente(id, nombres, apellidos, email, telefono,
                    ruc != null && !ruc.isBlank() ? new Ruc(ruc) : null,
                    razonSocial != null && !razonSocial.isBlank() ? new RazonSocial(razonSocial) : null,
                    rolEnum);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(Model model,
                                @RequestParam(required = false) String fecha,
                                @RequestParam(required = false) String estado) {
        model.addAttribute("fecha", fecha);
        model.addAttribute("estado", estado);
        model.addAttribute("pedidos", loadPedidos(fecha, estado));
        return "admin/pedidos";
    }

    @PostMapping("/cambiarEstadoPedido")
    public RedirectView cambiarEstadoPedido(@RequestParam("id_pedido") Long pedidoId,
                                            @RequestParam("estado") String nuevoEstado,
                                            RedirectAttributes redirectAttributes) {
        try {
            int updated = jdbcTemplate.update("UPDATE pedido SET estado = ? WHERE id_pedido = ?", nuevoEstado, pedidoId);
            if (updated == 0) {
                redirectAttributes.addFlashAttribute("error", "No se encontró el pedido.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente.");
            }
        } catch (DataAccessException ex) {
            logger.error("Error actualizando estado de pedido", ex);
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el estado: " + ex.getMessage());
        }
        RedirectView redirectView = new RedirectView("/admin/pedidos", true);
        redirectView.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
        return redirectView;
    }

    @GetMapping("/produccion")
    public String reporteProduccion(Model model) {
        model.addAttribute("lowStockProducts", loadLowStockProducts());
        return "admin/produccion";
    }

    private int queryCount(String sql) {
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException ex) {
            return 0;
        }
    }

    private List<PedidoResumen> loadRecentOrders() {
        try {
            return jdbcTemplate.query("SELECT p.id_pedido, c.email, p.estado, p.fecha_entrega, p.costo_total " +
                    "FROM pedido p LEFT JOIN cliente c ON p.id_cliente = c.id_cliente " +
                    "ORDER BY p.fecha_registro DESC LIMIT 20", (rs, rowNum) -> new PedidoResumen(
                    rs.getInt("id_pedido"),
                    rs.getString("email"),
                    rs.getString("estado"),
                    Optional.ofNullable(rs.getDate("fecha_entrega")).map(Date::toString).orElse("-"),
                    rs.getBigDecimal("costo_total")
            ));
        } catch (DataAccessException ex) {
            return List.of();
        }
    }

    private List<PedidoResumen> loadPedidos(String fechaEntrega, String estado) {
        try {
            StringBuilder sql = new StringBuilder("SELECT p.id_pedido, c.email, p.estado, p.fecha_entrega, p.costo_total " +
                    "FROM pedido p LEFT JOIN cliente c ON p.id_cliente = c.id_cliente");
            List<Object> params = new ArrayList<>();

            if (fechaEntrega != null && !fechaEntrega.isBlank()) {
                sql.append(" WHERE p.fecha_entrega = ?");
                params.add(Date.valueOf(fechaEntrega));
            }
            if (estado != null && !estado.isBlank()) {
                sql.append(params.isEmpty() ? " WHERE " : " AND ");
                sql.append("p.estado = ?");
                params.add(estado);
            }
            sql.append(" ORDER BY p.fecha_registro DESC LIMIT 50");

            return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new PedidoResumen(
                    rs.getInt("id_pedido"),
                    rs.getString("email"),
                    rs.getString("estado"),
                    Optional.ofNullable(rs.getDate("fecha_entrega")).map(Date::toString).orElse("-"),
                    rs.getBigDecimal("costo_total")
            ));
        } catch (DataAccessException ex) {
            return List.of();
        }
    }

    private List<LowStockProducto> loadLowStockProducts() {
        try {
            return jdbcTemplate.query("SELECT nombre, stock_minimo AS stock, stock_minimo, categoria " +
                    "FROM producto WHERE stock_minimo <= 0 ORDER BY stock_minimo ASC LIMIT 10", (rs, rowNum) -> new LowStockProducto(
                    rs.getString("nombre"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo"),
                    rs.getString("categoria")
            ));
        } catch (DataAccessException ex) {
            return List.of();
        }
    }

    public record PedidoResumen(Integer id, String clienteEmail, String estado, String fechaEntrega, BigDecimal total) {
    }

    public record LowStockProducto(String nombre, Integer stock, Integer stockMinimo, String categoria) {
    }
}
