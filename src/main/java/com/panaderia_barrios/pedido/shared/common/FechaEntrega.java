package com.panaderia_barrios.pedido.shared.common;

import java.time.LocalDate;

public class FechaEntrega {
    private final LocalDate fecha;

    public FechaEntrega(String fecha) {
        LocalDate fechaIngresada = LocalDate.parse(fecha);
        LocalDate fechaMinima = LocalDate.now().plusDays(1);

        if (fechaIngresada.isBefore(fechaMinima)) {
            throw new IllegalArgumentException("La fecha debe ser al menos mañana.");
        }

        this.fecha = fechaIngresada;
    }

    public String getFecha() {
        return fecha.toString();
    }

    public LocalDate getLocalDate() {
        return fecha;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FechaEntrega that = (FechaEntrega) obj;
        return fecha.equals(that.fecha);
    }

    @Override
    public int hashCode() {
        return fecha.hashCode();
    }

    @Override
    public String toString() {
        return "FechaEntrega{" + "fecha=" + fecha + '}';
    }
}