package com.panaderia_barrios.pedido.catalogo.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaProductoRepository extends JpaRepository<ProductoEntity, Integer> {

    @Query(value = "SELECT * FROM producto WHERE disponible_b2c = true ORDER BY nombre", nativeQuery = true)
    List<ProductoEntity> findCatalogoB2C();

    @Query(value = "SELECT * FROM producto WHERE disponible_b2b = true ORDER BY nombre", nativeQuery = true)
    List<ProductoEntity> findCatalogoB2B();
}