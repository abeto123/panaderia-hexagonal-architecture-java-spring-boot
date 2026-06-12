package com.panaderia.ecommerce.catalog.infrastructure.jpa;

import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.CategoriaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoriaRepositoryJpa implements CategoriaRepository {
    private final JpaCategoriaRepository jpaRepository;
    private final ProductoMapper mapper;

    public CategoriaRepositoryJpa(JpaCategoriaRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::mapCategoria);
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::mapCategoria);
    }

    @Override
    public List<Categoria> findAll() {
        return jpaRepository.findAll().stream().map(mapper::mapCategoria).collect(Collectors.toList());
    }
}
