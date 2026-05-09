package com.panaderia_barrios.pedido.shared.common;

public class Dinero {
    private final double monto;

    public Dinero(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        this.monto = monto;
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(this.monto + otro.monto);
    }

    public double getMonto() {
        return monto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dinero dinero = (Dinero) obj;
        return Double.compare(dinero.monto, monto) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(monto);
    }

    @Override
    public String toString() {
        return "Dinero{" + "monto=" + monto + '}';
    }
}