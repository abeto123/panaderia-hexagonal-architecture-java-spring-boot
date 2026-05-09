package com.panaderia_barrios.pedido.carrito.domain.ports.output;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.CarritoItem;

public interface CarritoRepository {
    Carrito obtenerPorClienteId(int clienteId);
    Carrito agregarOActualizarItem(int clienteId, CarritoItem item);
    boolean eliminarItem(int clienteId, int productoId);
    void vaciar(int clienteId);
}