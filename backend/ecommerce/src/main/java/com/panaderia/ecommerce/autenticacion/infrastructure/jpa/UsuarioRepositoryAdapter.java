package com.panaderia.ecommerce.autenticacion.infrastructure.jpa;

import com.panaderia.ecommerce.autenticacion.domain.Usuario;
import com.panaderia.ecommerce.autenticacion.domain.UsuarioRepository;
import com.panaderia.ecommerce.autenticacion.infrastructure.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioRepositoryJpa usuarioRepositoryJpa;

    public UsuarioRepositoryAdapter(UsuarioRepositoryJpa usuarioRepositoryJpa) {
        this.usuarioRepositoryJpa = usuarioRepositoryJpa;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepositoryJpa.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepositoryJpa.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity saved = usuarioRepositoryJpa.save(entity);
        return toDomain(saved);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(entity.getId(), entity.getEmail(), entity.getPasswordHash(), 
                           entity.getRol(), entity.getId());
    }

    private UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        if (usuario.getId() != null) {
            entity.setId(usuario.getId());
        }
        entity.setEmail(usuario.getEmail());
        entity.setPasswordHash(usuario.getPasswordHash());
        entity.setRol(usuario.getRol());
        return entity;
    }
}
