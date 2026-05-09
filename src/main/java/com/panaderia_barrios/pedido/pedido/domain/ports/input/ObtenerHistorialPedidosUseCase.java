package com.panaderia_barrios.pedido.pedido.domain.ports.input;

import java.util.List;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;

public interface ObtenerHistorialPedidosUseCase {
    List<Pedido> obtenerPorCliente(int clienteId);
}