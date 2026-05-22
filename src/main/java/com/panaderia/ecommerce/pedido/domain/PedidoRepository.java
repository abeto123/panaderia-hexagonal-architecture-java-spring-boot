package com.panaderia.ecommerce.pedido.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByFechaEntrega(LocalDate fecha);
    Pedido save(Pedido pedido);
    void deleteById(Long id);
}