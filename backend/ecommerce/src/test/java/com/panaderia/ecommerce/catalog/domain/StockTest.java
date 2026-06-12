package com.panaderia.ecommerce.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void crearStockValido() {
        Stock stock = new Stock(100);
        assertEquals(100, stock.getCantidad());
    }

    @Test
    void stockNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Stock(-1));
    }

    @Test
    void stockExcesivoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Stock(10000));
    }

    @Test
    void tieneSuficienteStock() {
        Stock stock = new Stock(50);
        assertTrue(stock.tieneSuficiente(30));
        assertFalse(stock.tieneSuficiente(60));
    }

    @Test
    void disminuirStock() {
        Stock stock = new Stock(50);
        Stock nuevo = stock.disminuir(20);
        assertEquals(30, nuevo.getCantidad());
    }

    @Test
    void disminuirMasQueDisponibleLanzaExcepcion() {
        Stock stock = new Stock(10);
        assertThrows(IllegalArgumentException.class, () -> stock.disminuir(20));
    }
}