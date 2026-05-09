package com.panaderia_barrios.pedido.pedido.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaPedidoProductoRepository extends JpaRepository<PedidoProductoEntity, Long> {
    List<PedidoProductoEntity> findByIdPedido(Long idPedido);
}