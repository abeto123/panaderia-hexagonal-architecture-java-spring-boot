package com.panaderia_barrios.pedido.carrito.domain.ports.input;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.CarritoItem;

public interface AgregarProductoCarritoUseCase {
    Carrito agregar(int clienteId, CarritoItem item);
}