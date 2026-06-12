package com.panaderia.ecommerce.catalog.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Precio {
    private final BigDecimal valor;
    private final Currency moneda;

    public Precio(BigDecimal valor, Currency moneda) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio debe ser positivo");
        }
        this.valor = valor;
        this.moneda = moneda != null ? moneda : Currency.getInstance("PEN");
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Currency getMoneda() {
        return moneda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Precio precio = (Precio) o;
        return valor.equals(precio.valor) && moneda.equals(precio.moneda);
    }

    @Override
    public int hashCode() {
        return valor.hashCode() + moneda.hashCode();
    }

    @Override
    public String toString() {
        return valor + " " + moneda.getCurrencyCode();
    }
}