package com.panaderia.ecommerce.catalog.domain;

public class Stock {
    private final int cantidad;

    public Stock(int cantidad) {
        if (cantidad < 0 || cantidad > 9999) {
            throw new IllegalArgumentException("Stock debe estar entre 0 y 9999");
        }
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean tieneSuficiente(int requerido) {
        return cantidad >= requerido;
    }

    public Stock disminuir(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("Cantidad a disminuir debe ser positiva");
        }
        int nuevaCantidad = this.cantidad - cantidad;
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        return new Stock(nuevaCantidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return cantidad == stock.cantidad;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(cantidad);
    }

    @Override
    public String toString() {
        return String.valueOf(cantidad);
    }
}