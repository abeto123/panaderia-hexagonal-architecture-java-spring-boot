package com.panaderia.ecommerce.pedido.domain;

import java.time.LocalDate;

public class FechaEntrega {
    private final LocalDate fecha;

    public FechaEntrega(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return fecha.toString();
    }
}