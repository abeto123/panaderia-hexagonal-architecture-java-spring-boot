package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.cliente.domain.Cliente;
import com.panaderia_barrios.pedido.cliente.domain.output.ClienteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final JpaClienteRepository jpaRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteRepositoryAdapter(JpaClienteRepository jpaRepository, PasswordEncoder passwordEncoder) {
        this.jpaRepository = jpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    private ClienteEntity toEntity(Cliente cliente) {
        String hashedPassword = passwordEncoder.encode(cliente.getContrasenia());
        return new ClienteEntity(
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                hashedPassword,
                cliente.getRol(),
                cliente.getRuc(),
                cliente.getRazonSocial()
        );
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(
                entity.getId(),
                entity.getNombre(),
                entity.getApellidos(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getContrasenia(),
                entity.getRol(),
                entity.getRuc(),
                entity.getRazonSocial()
        );
    }
}