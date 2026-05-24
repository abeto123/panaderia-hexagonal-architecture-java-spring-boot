package com.panaderia.ecommerce.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PrecioTest {

    @Test
    void crearPrecioValido() {
        BigDecimal valor = BigDecimal.valueOf(25.50);
        Currency moneda = Currency.getInstance("PEN");
        Precio precio = new Precio(valor, moneda);

        assertEquals(valor, precio.getValor());
        assertEquals(moneda, precio.getMoneda());
    }

    @Test
    void precioNegativoLanzaExcepcion() {
        BigDecimal valor = BigDecimal.valueOf(-10.0);
        Currency moneda = Currency.getInstance("PEN");

        assertThrows(IllegalArgumentException.class, () -> new Precio(valor, moneda));
    }

    @Test
    void precioNuloLanzaExcepcion() {
        Currency moneda = Currency.getInstance("PEN");

        assertThrows(IllegalArgumentException.class, () -> new Precio(null, moneda));
    }
}