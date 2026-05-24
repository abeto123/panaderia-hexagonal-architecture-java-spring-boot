package com.panaderia.ecommerce.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Optional<Categoria> findById(Long id);
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findAll();
}
