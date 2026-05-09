package com.panaderia_barrios.pedido.pedido.domain.ports.output;

import java.util.List;
import java.util.Optional;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;

public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
    List<Pedido> findByClienteId(int clienteId);
    // Other methods as needed
}