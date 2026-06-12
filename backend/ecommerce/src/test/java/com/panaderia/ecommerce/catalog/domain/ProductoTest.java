package com.panaderia.ecommerce.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void crearProductoValido() {
        Precio precio = new Precio(BigDecimal.valueOf(10.0), Currency.getInstance("PEN"));
        Stock stock = new Stock(50);
        Categoria categoria = new Categoria(1L, "Panaderia");
        Producto producto = new Producto(1L, "Pan", "Delicioso", categoria, precio, stock, "/img.jpg");

        assertEquals(1L, producto.getId());
        assertEquals("Pan", producto.getNombre());
        assertEquals(precio, producto.getPrecio());
        assertEquals(stock, producto.getStock());
    }

    @Test
    void disminuirStock() {
        Precio precio = new Precio(BigDecimal.valueOf(10.0), Currency.getInstance("PEN"));
        Stock stock = new Stock(50);
        Categoria categoria = new Categoria(1L, "Panaderia");
        Producto producto = new Producto(1L, "Pan", "Delicioso", categoria, precio, stock, "/img.jpg");

        Producto actualizado = producto.disminuirStock(10);

        assertEquals(40, actualizado.getStock().getCantidad());
    }

    @Test
    void stockInsuficienteLanzaExcepcion() {
        Precio precio = new Precio(BigDecimal.valueOf(10.0), Currency.getInstance("PEN"));
        Stock stock = new Stock(5);
        Categoria categoria = new Categoria(1L, "Panaderia");
        Producto producto = new Producto(1L, "Pan", "Delicioso", categoria, precio, stock, "/img.jpg");

        assertThrows(IllegalArgumentException.class, () -> producto.disminuirStock(10));
    }
}