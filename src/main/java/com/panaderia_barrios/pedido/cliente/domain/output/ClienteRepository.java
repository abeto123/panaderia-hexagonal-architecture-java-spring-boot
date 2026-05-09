package com.panaderia_barrios.pedido.cliente.domain.output;

import java.util.Optional;

import com.panaderia_barrios.pedido.cliente.domain.Cliente;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(int id);
    Optional<Cliente> findByEmail(String email);
}