package com.panaderia_barrios.pedido.pedido.domain.ports.input;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;

public interface CrearPedidoUseCase {
    Pedido crear(Pedido pedido);
}