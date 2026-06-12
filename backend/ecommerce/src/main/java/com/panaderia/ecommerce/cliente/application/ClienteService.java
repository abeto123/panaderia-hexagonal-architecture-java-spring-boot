package com.panaderia.ecommerce.cliente.application;

import com.panaderia.ecommerce.autenticacion.domain.Rol;
import com.panaderia.ecommerce.autenticacion.domain.Usuario;
import com.panaderia.ecommerce.autenticacion.domain.UsuarioRepository;
import com.panaderia.ecommerce.cliente.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, 
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerCliente(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Cliente crearCliente(String nombres, String apellidos, String email, String telefono, Ruc ruc, RazonSocial razonSocial, com.panaderia.ecommerce.autenticacion.domain.Rol rol) {
        Cliente cliente = new Cliente(null, nombres, apellidos, email, telefono, ruc, razonSocial, List.of(), rol);
        return clienteRepository.save(cliente);
    }

    public Cliente editarCliente(Long id, String nombres, String apellidos, String email, String telefono, Ruc ruc, RazonSocial razonSocial, com.panaderia.ecommerce.autenticacion.domain.Rol rol) {
        Optional<Cliente> existente = clienteRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        com.panaderia.ecommerce.autenticacion.domain.Rol rolToUse = rol != null ? rol : existente.get().getRol();
        Cliente cliente = new Cliente(id, nombres, apellidos, email, telefono, ruc, razonSocial, existente.get().getDirecciones(), rolToUse);
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente agregarDireccion(Long clienteId, Direccion direccion) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Cliente clienteActualizado = cliente.agregarDireccion(direccion);
        return clienteRepository.save(clienteActualizado);
    }

    public Cliente eliminarDireccion(Long clienteId, String alias) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Cliente clienteActualizado = cliente.eliminarDireccion(alias);
        return clienteRepository.save(clienteActualizado);
    }

    public Cliente crearClienteConUsuario(String nombres, String apellidos, String email, 
                                           String telefono, String ruc, String razonSocial, String password) {
        // Verificar que el email no existe
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear cliente
        Ruc rucVO = ruc != null && !ruc.isEmpty() ? new Ruc(ruc) : null;
        RazonSocial razonSocialVO = razonSocial != null && !razonSocial.isEmpty() ? new RazonSocial(razonSocial) : null;
        Cliente cliente = new Cliente(null, nombres, apellidos, email, telefono, rucVO, razonSocialVO, List.of(), null);
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // Crear usuario
        String passwordHash = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(clienteGuardado.getId(), email, passwordHash, Rol.CLIENTE, clienteGuardado.getId());
        usuarioRepository.save(usuario);

        return clienteGuardado;
    }
}