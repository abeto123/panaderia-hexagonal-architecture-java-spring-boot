package com.panaderia.ecommerce.autenticacion.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
}