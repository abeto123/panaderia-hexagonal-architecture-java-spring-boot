package com.panaderia.ecommerce.catalog.infrastructure.jpa;

import com.panaderia.ecommerce.catalog.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoRepositoryJpa implements ProductoRepository {
    private final JpaProductoRepository jpaRepository;
    private final ProductoMapper mapper;

    public ProductoRepositoryJpa(JpaProductoRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return mapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = mapper.toEntity(producto);
        ProductoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Producto> findByCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            return List.of();
        }
        CategoriaEntity categoriaEntity = new CategoriaEntity();
        categoriaEntity.setId(categoria.getId());
        return mapper.toDomain(jpaRepository.findByCategoria(categoriaEntity));
    }
}