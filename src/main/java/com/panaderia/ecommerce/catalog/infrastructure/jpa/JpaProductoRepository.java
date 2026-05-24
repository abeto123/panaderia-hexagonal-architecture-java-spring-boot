package com.panaderia.ecommerce.catalog.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByCategoria(CategoriaEntity categoria);
}