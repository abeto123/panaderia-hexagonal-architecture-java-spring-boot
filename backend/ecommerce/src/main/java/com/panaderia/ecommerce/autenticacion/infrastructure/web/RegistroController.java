package com.panaderia.ecommerce.autenticacion.infrastructure.web;

import com.panaderia.ecommerce.cliente.application.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    private final ClienteService clienteService;

    public RegistroController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarCliente(
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String ruc,
            @RequestParam(required = false) String razonSocial,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.crearClienteConUsuario(nombres, apellidos, email, telefono, ruc, razonSocial, password);
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor inicia sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrarse: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}
