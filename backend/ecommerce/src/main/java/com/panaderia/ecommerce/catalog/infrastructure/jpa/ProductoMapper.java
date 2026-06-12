package com.panaderia.ecommerce.catalog.infrastructure.jpa;

import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.catalog.domain.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Currency;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "categoria", expression = "java(mapCategoria(entity.getCategoria()))")
    @Mapping(target = "precio", expression = "java(new Precio(entity.getPrecio(), Currency.getInstance(\"PEN\")))")
    @Mapping(target = "stock", expression = "java(new Stock(entity.getStockMinimo()))")
    Producto toDomain(ProductoEntity entity);

    List<Producto> toDomain(List<ProductoEntity> entities);

    @Mapping(target = "categoria", expression = "java(mapCategoriaEntity(domain.getCategoria()))")
    @Mapping(target = "precio", expression = "java(domain.getPrecio().getValor())")
    @Mapping(target = "stockMinimo", expression = "java(domain.getStock().getCantidad())")
    @Mapping(target = "disponible", constant = "true")
    ProductoEntity toEntity(Producto domain);

    default Categoria mapCategoria(CategoriaEntity entity) {
        return entity == null ? null : new Categoria(entity.getId(), entity.getNombre());
    }

    default CategoriaEntity mapCategoriaEntity(Categoria domain) {
        if (domain == null) {
            return null;
        }
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        return entity;
    }
}