package com.panaderia.ecommerce.cliente.infrastructure.web;

import com.panaderia.ecommerce.cliente.application.ClienteService;
import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.cliente.domain.RazonSocial;
import com.panaderia.ecommerce.cliente.domain.Ruc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Área de autogestión del cliente autenticado: perfil y direcciones propias.
 * Sigue el mismo patrón que AdminController para la tabla `direccion`
 * (acceso directo por JDBC), ya que ClienteService.agregarDireccion/eliminarDireccion
 * no persisten en base de datos (el campo direcciones de ClienteEntity es @Transient).
 */
@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final JdbcTemplate jdbcTemplate;

    public ClienteController(ClienteService clienteService, JdbcTemplate jdbcTemplate) {
        this.clienteService = clienteService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Cliente clienteActual(Authentication authentication) {
        String email = authentication.getName();
        return clienteService.obtenerClientePorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    // ---------- Perfil ----------

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication authentication) {
        model.addAttribute("cliente", clienteActual(authentication));
        return "cliente/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombres,
                                    @RequestParam String apellidos,
                                    @RequestParam(required = false) String telefono,
                                    @RequestParam(required = false) String ruc,
                                    @RequestParam(required = false) String razonSocial,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            clienteService.editarCliente(actual.getId(), nombres, apellidos, actual.getEmail(), telefono,
                    ruc != null && !ruc.isBlank() ? new Ruc(ruc) : null,
                    razonSocial != null && !razonSocial.isBlank() ? new RazonSocial(razonSocial) : null,
                    actual.getRol());
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el perfil: " + e.getMessage());
        }
        return "redirect:/cliente/perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                   @RequestParam String passwordNueva,
                                   @RequestParam String passwordConfirmar,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (!passwordNueva.equals(passwordConfirmar)) {
                throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
            }
            if (passwordNueva.length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
            }
            clienteService.cambiarPassword(authentication.getName(), passwordActual, passwordNueva);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar la contraseña: " + e.getMessage());
        }
        return "redirect:/cliente/perfil";
    }

    // ---------- Direcciones ----------

    @GetMapping("/direcciones")
    public String listarDirecciones(Model model, Authentication authentication) {
        Cliente actual = clienteActual(authentication);
        List<Map<String, Object>> direcciones = jdbcTemplate.queryForList(
                "SELECT d.id_direccion AS id, d.alias, d.calle, d.numero, COALESCE(dist.nombre, '') AS distrito, d.referencia " +
                        "FROM direccion d LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito WHERE d.id_cliente = ? " +
                        "ORDER BY d.id_direccion",
                actual.getId());
        model.addAttribute("cliente", actual);
        model.addAttribute("direcciones", direcciones);
        return "cliente/direcciones";
    }

    @PostMapping("/direcciones")
    public String agregarDireccion(@RequestParam(required = false) String alias,
                                    @RequestParam String calle,
                                    @RequestParam(required = false) String numero,
                                    @RequestParam(required = false) Long id_distrito,
                                    @RequestParam(required = false) String referencia,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            if (id_distrito != null) {
                jdbcTemplate.update("INSERT INTO direccion (calle, numero, referencia, alias, id_cliente, id_distrito) VALUES (?, ?, ?, ?, ?, ?)",
                        calle, numero, referencia, alias, actual.getId(), id_distrito);
            } else {
                jdbcTemplate.update("INSERT INTO direccion (calle, numero, referencia, alias, id_cliente, id_distrito) VALUES (?, ?, ?, ?, ?, NULL)",
                        calle, numero, referencia, alias, actual.getId());
            }
            redirectAttributes.addFlashAttribute("success", "Dirección agregada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar la dirección: " + e.getMessage());
        }
        return "redirect:/cliente/direcciones";
    }

    @PostMapping("/direcciones/{dirId}/eliminar")
    public String eliminarDireccion(@PathVariable Long dirId, Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            Cliente actual = clienteActual(authentication);
            // El AND id_cliente = ? evita que un cliente borre direcciones de otro cliente
            int filas = jdbcTemplate.update("DELETE FROM direccion WHERE id_direccion = ? AND id_cliente = ?",
                    dirId, actual.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("Dirección no encontrada");
            }
            redirectAttributes.addFlashAttribute("success", "Dirección eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la dirección: " + e.getMessage());
        }
        return "redirect:/cliente/direcciones";
    }
}
