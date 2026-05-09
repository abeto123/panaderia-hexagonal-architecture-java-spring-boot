package com.panaderia_barrios.pedido.carrito.domain.ports.input;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;

public interface ObtenerCarritoUseCase {
    Carrito obtener(int clienteId);
}