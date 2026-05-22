package com.panaderia.ecommerce.autenticacion.infrastructure.jpa;

import com.panaderia.ecommerce.autenticacion.infrastructure.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositoryJpa extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
}
