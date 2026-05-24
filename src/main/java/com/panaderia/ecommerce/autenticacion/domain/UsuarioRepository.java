package com.panaderia.ecommerce.autenticacion.domain;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
}