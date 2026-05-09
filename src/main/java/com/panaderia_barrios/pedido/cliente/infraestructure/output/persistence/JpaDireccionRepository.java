package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaDireccionRepository extends JpaRepository<DireccionEntity, Integer> {
    List<DireccionEntity> findByIdCliente(int idCliente);
}