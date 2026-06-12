package com.panaderia.ecommerce.cliente.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByEmail(String email);
}
