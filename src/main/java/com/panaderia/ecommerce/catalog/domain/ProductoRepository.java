package com.panaderia.ecommerce.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Optional<Producto> findById(Long id);
    List<Producto> findAll();
    Producto save(Producto producto);
    void deleteById(Long id);
    List<Producto> findByCategoria(Categoria categoria);
}