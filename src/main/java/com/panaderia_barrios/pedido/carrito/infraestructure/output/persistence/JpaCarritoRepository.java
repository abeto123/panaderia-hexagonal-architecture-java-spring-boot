package com.panaderia_barrios.pedido.carrito.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaCarritoRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findFirstByIdCliente(int idCliente);
}