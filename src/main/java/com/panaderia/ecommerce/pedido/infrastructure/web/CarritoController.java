package com.panaderia.ecommerce.pedido.infrastructure.web;

import com.panaderia.ecommerce.cliente.application.ClienteService;
import com.panaderia.ecommerce.cliente.domain.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Carrito de compras del cliente autenticado. Persistido en las tablas
 * `carrito` / `carrito_producto` (ya existentes en schema.sql).
 */
@Controller
@RequestMapping("/cliente/carrito")
public class CarritoController {

    private final ClienteService clienteService;
    private final JdbcTemplate jdbcTemplate;

    public CarritoController(ClienteService clienteService, JdbcTemplate jdbcTemplate) {
        this.clienteService = clienteService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Cliente clienteActual(Authentication authentication) {
        return clienteService.obtenerClientePorEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    private Long obtenerOCrearCarritoId(Long clienteId) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id_carrito FROM carrito WHERE id_cliente = ?",
                new Object[]{clienteId},
                (rs, rowNum) -> rs.getLong("id_carrito"));
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO carrito (id_cliente) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, clienteId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @GetMapping
    public String verCarrito(Model model, Authentication authentication) {
        Cliente actual = clienteActual(authentication);
        Long carritoId = obtenerOCrearCarritoId(actual.getId());

        List<ItemCarritoDTO> items = jdbcTemplate.query(
                "SELECT cp.id_producto, p.nombre, p.foto, p.precio, p.stock_minimo, p.compra_minima, cp.cantidad, (p.precio * cp.cantidad) AS subtotal " +
                        "FROM carrito_producto cp JOIN producto p ON cp.id_producto = p.id_producto " +
                        "WHERE cp.id_carrito = ? ORDER BY p.nombre",
                new Object[]{carritoId},
                (rs, rowNum) -> new ItemCarritoDTO(
                        rs.getLong("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("foto"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock_minimo"),
                        rs.getInt("compra_minima"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("subtotal")
                ));

        BigDecimal total = items.stream().map(ItemCarritoDTO::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cliente/carrito";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long productoId,
                           @RequestParam(defaultValue = "1") int cantidad,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
            Cliente actual = clienteActual(authentication);
            Long carritoId = obtenerOCrearCarritoId(actual.getId());

            List<Map<String, Object>> productoInfo = jdbcTemplate.queryForList(
                    "SELECT stock_minimo, compra_minima FROM producto WHERE id_producto = ? AND disponible = TRUE",
                    productoId);
            if (productoInfo.isEmpty()) {
                throw new IllegalArgumentException("Producto no disponible");
            }
            int compraMinima = ((Number) productoInfo.get(0).get("compra_minima")).intValue();

            List<Integer> cantidadActualList = jdbcTemplate.query(
                    "SELECT cantidad FROM carrito_producto WHERE id_carrito = ? AND id_producto = ?",
                    new Object[]{carritoId, productoId}, (rs, rowNum) -> rs.getInt(1));
            int cantidadActual = cantidadActualList.isEmpty() ? 0 : cantidadActualList.get(0);
            int cantidadFinal = cantidadActual + cantidad;

            if (cantidadFinal < compraMinima) {
                throw new IllegalArgumentException(
                        "La compra mínima de este producto es " + compraMinima + " unidades");
            }

            int actualizados = jdbcTemplate.update(
                    "UPDATE carrito_producto SET cantidad = cantidad + ? WHERE id_carrito = ? AND id_producto = ?",
                    cantidad, carritoId, productoId);
            if (actualizados == 0) {
                jdbcTemplate.update(
                        "INSERT INTO carrito_producto (id_carrito, id_producto, cantidad) VALUES (?, ?, ?)",
                        carritoId, productoId, cantidad);
            }
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar el producto: " + e.getMessage());
        }
        return "redirect:/productos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                              @RequestParam int cantidad,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            Long carritoId = obtenerOCrearCarritoId(actual.getId());
            if (cantidad <= 0) {
                jdbcTemplate.update("DELETE FROM carrito_producto WHERE id_carrito = ? AND id_producto = ?", carritoId, productoId);
            } else {
                List<Integer> compraMinimaList = jdbcTemplate.query(
                        "SELECT compra_minima FROM producto WHERE id_producto = ?",
                        new Object[]{productoId}, (rs, rowNum) -> rs.getInt(1));
                int compraMinima = compraMinimaList.isEmpty() ? 1 : compraMinimaList.get(0);
                if (cantidad < compraMinima) {
                    throw new IllegalArgumentException(
                            "La compra mínima de este producto es " + compraMinima + " unidades");
                }
                jdbcTemplate.update("UPDATE carrito_producto SET cantidad = ? WHERE id_carrito = ? AND id_producto = ?",
                        cantidad, carritoId, productoId);
            }
            redirectAttributes.addFlashAttribute("success", "Carrito actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el carrito: " + e.getMessage());
        }
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/eliminar/{productoId}")
    public String eliminar(@PathVariable Long productoId, Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            Long carritoId = obtenerOCrearCarritoId(actual.getId());
            jdbcTemplate.update("DELETE FROM carrito_producto WHERE id_carrito = ? AND id_producto = ?", carritoId, productoId);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el producto: " + e.getMessage());
        }
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciar(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            Long carritoId = obtenerOCrearCarritoId(actual.getId());
            jdbcTemplate.update("DELETE FROM carrito_producto WHERE id_carrito = ?", carritoId);
            redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo vaciar el carrito: " + e.getMessage());
        }
        return "redirect:/cliente/carrito";
    }

    public record ItemCarritoDTO(Long productoId, String nombre, String foto, BigDecimal precioUnitario,
                                  int stockDisponible, int compraMinima, int cantidad, BigDecimal subtotal) {
    }
}
