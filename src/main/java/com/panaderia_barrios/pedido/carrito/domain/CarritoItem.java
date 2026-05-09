package com.panaderia_barrios.pedido.carrito.domain;

public class CarritoItem {
    private final int productoId;
    private final int cantidad;
    private final Double montoSolicitadoEntero;
    private final String nombre;
    private final String foto;
    private final double precioUnitario;

    public CarritoItem(int productoId, int cantidad, Double montoSolicitadoEntero, String nombre, String foto, double precioUnitario) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.montoSolicitadoEntero = montoSolicitadoEntero;
        this.nombre = nombre;
        this.foto = foto;
        this.precioUnitario = precioUnitario;
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

    public String getNombre() {
        return nombre;
    }

    public String getFoto() {
        return foto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double calcularSubtotal() {
        return precioUnitario * cantidad;
    }
}