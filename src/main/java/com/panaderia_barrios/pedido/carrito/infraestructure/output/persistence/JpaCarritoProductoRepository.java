package com.panaderia_barrios.pedido.carrito.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCarritoProductoRepository extends JpaRepository<CarritoProductoEntity, Long> {
    List<CarritoProductoEntity> findByIdCarrito(Long idCarrito);
    Optional<CarritoProductoEntity> findByIdCarritoAndIdProducto(Long idCarrito, int idProducto);
    void deleteByIdCarritoAndIdProducto(Long idCarrito, int idProducto);
    void deleteByIdCarrito(Long idCarrito);
}