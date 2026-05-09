package com.panaderia_barrios.pedido.carrito.domain;

import java.util.Collections;
import java.util.List;

public class Carrito {
    private final int clienteId;
    private final List<CarritoItem> items;

    public Carrito(int clienteId, List<CarritoItem> items) {
        this.clienteId = clienteId;
        this.items = items != null ? items : Collections.emptyList();
    }

    public int getClienteId() {
        return clienteId;
    }

    public List<CarritoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double calcularTotal() {
        return items.stream().mapToDouble(CarritoItem::calcularSubtotal).sum();
    }
}