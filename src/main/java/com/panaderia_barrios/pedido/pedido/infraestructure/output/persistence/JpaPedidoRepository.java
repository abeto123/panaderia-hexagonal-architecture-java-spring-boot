package com.panaderia_barrios.pedido.pedido.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaPedidoRepository extends JpaRepository<PedidoEntity, Long> {
    List<PedidoEntity> findByIdCliente(int idCliente);
}