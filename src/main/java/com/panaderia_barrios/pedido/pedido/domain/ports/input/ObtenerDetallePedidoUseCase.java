package com.panaderia_barrios.pedido.pedido.domain.ports.input;

import java.util.Optional;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;

public interface ObtenerDetallePedidoUseCase {
    Optional<Pedido> obtenerPorId(Long pedidoId);
}