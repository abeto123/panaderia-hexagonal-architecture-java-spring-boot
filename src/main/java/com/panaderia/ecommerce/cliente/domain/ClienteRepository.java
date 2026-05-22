package com.panaderia.ecommerce.cliente.domain;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findAll();
    Cliente save(Cliente cliente);
    void deleteById(Long id);
}