package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaClienteRepository extends JpaRepository<ClienteEntity, Integer> {
    Optional<ClienteEntity> findByEmail(String email);
}