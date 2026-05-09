package com.panaderia_barrios.pedido.cliente.infraestructure.input.web;

import com.panaderia_barrios.pedido.cliente.domain.Cliente;
import com.panaderia_barrios.pedido.cliente.domain.input.RegistrarClienteUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final RegistrarClienteUseCase registrarClienteUseCase;

    public ClienteController(RegistrarClienteUseCase registrarClienteUseCase) {
        this.registrarClienteUseCase = registrarClienteUseCase;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Cliente> registrar(@RequestBody RegistrarClienteRequest request) {
        Cliente cliente = new Cliente(
                0, // ID will be generated
                request.getNombre(),
                request.getApellidos(),
                request.getEmail(),
                request.getTelefono(),
                request.getContrasenia(),
                request.isB2b() ? "EMPRESA_FACTURA" : "CLIENTE_ESTANDAR",
                request.getRuc(),
                request.getRazonSocial()
        );
        Cliente registrado = registrarClienteUseCase.registrar(cliente);
        return ResponseEntity.ok(registrado);
    }

    // DTO
    public static class RegistrarClienteRequest {
        private String nombre;
        private String apellidos;
        private String email;
        private String telefono;
        private String contrasenia;
        private boolean b2b;
        private String ruc;
        private String razonSocial;

        // Getters and setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public String getContrasenia() { return contrasenia; }
        public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

        public boolean isB2b() { return b2b; }
        public void setB2b(boolean b2b) { this.b2b = b2b; }

        public String getRuc() { return ruc; }
        public void setRuc(String ruc) { this.ruc = ruc; }

        public String getRazonSocial() { return razonSocial; }
        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    }
}