package com.panaderia_barrios.pedido.carrito.domain.ports.input;

public interface EliminarProductoCarritoUseCase {
    boolean eliminar(int clienteId, int productoId);
}