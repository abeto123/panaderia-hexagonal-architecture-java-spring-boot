package com.panaderia_barrios.pedido.catalogo.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;
import com.panaderia_barrios.pedido.catalogo.domain.ports.output.ProductoRepository;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductoRepositoryAdapter implements ProductoRepository {

    private final JpaProductoRepository jpaRepository;

    public ProductoRepositoryAdapter(JpaProductoRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Producto> findCatalogoB2C() {
        return jpaRepository.findCatalogoB2C().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findCatalogoB2B() {
        return jpaRepository.findCatalogoB2B().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> findById(int id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = toEntity(producto);
        ProductoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(int id) {
        jpaRepository.deleteById(id);
    }

    private ProductoEntity toEntity(Producto producto) {
        return new ProductoEntity(
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecioB2c(),
                producto.getUnidadesBaseB2b(),
                producto.getSolesBaseB2b(),
                producto.getUnidadMinimaB2b(),
                producto.isDisponibleB2b(),
                producto.getIdCategoria(),
                producto.getFoto()
        );
    }

    private Producto toDomain(ProductoEntity entity) {
        return new Producto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecioB2c(),
                entity.getUnidadesBaseB2b(),
                entity.getSolesBaseB2b(),
                entity.getUnidadMinimaB2b(),
                entity.isDisponibleB2b(),
                entity.getIdCategoria(),
                entity.getFoto()
        );
    }
}