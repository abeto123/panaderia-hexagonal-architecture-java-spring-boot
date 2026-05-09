package com.panaderia_barrios.pedido.pedido.domain;

public class PedidoItem {
    private final int productoId;
    private final int cantidad;
    private final Double montoSolicitadoEntero;
    private final double precioUnitarioCongelado;
    private final double subtotal;

    public PedidoItem(int productoId, int cantidad, Double montoSolicitadoEntero, double precioUnitarioCongelado, double subtotal) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.montoSolicitadoEntero = montoSolicitadoEntero;
        this.precioUnitarioCongelado = precioUnitarioCongelado;
        this.subtotal = subtotal;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Double getMontoSolicitadoEntero() {
        return montoSolicitadoEntero;
    }

    public double getPrecioUnitarioCongelado() {
        return precioUnitarioCongelado;
    }

    public double getSubtotal() {
        return subtotal;
    }
}