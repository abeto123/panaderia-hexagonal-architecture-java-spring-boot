package com.panaderia.ecommerce.pedido.domain;

import com.panaderia.ecommerce.catalog.domain.Precio;

import java.math.BigDecimal;

public class ItemPedido {
    private final Long productoId;
    private final String nombreProducto;
    private final int cantidad;
    private final Precio precioUnitario;

    public ItemPedido(Long productoId, String nombreProducto, int cantidad, Precio precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Precio getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.getValor().multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public String toString() {
        return nombreProducto + " x" + cantidad + " = " + getSubtotal();
    }
}