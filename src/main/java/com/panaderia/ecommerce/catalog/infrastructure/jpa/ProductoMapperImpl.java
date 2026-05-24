package com.panaderia.ecommerce.catalog.infrastructure.jpa;

import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.catalog.domain.Stock;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }
        Precio precio = new Precio(entity.getPrecio(), Currency.getInstance("PEN"));
        Stock stock = new Stock(entity.getStockMinimo());
        Categoria categoria = mapCategoria(entity.getCategoria());
        return new Producto(entity.getId(), entity.getNombre(), entity.getDescripcion(), categoria, precio, stock, entity.getDisponible() != null && entity.getDisponible(), entity.getImagenUrl());
    }

    @Override
    public List<Producto> toDomain(List<ProductoEntity> entities) {
        return entities == null ? List.of() : entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) {
            return null;
        }
        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setCategoria(mapCategoriaEntity(domain.getCategoria()));
        entity.setPrecio(domain.getPrecio().getValor());
        entity.setStockMinimo(domain.getStock().getCantidad());
        entity.setDisponible(domain.isDisponible());
        entity.setImagenUrl(domain.getImagenUrl());
        return entity;
    }

    @Override
    public Categoria mapCategoria(CategoriaEntity entity) {
        return entity == null ? null : new Categoria(entity.getId(), entity.getNombre());
    }

    @Override
    public CategoriaEntity mapCategoriaEntity(Categoria domain) {
        if (domain == null) {
            return null;
        }
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        return entity;
    }
}
