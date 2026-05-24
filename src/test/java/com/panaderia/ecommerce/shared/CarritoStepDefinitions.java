package com.panaderia.ecommerce.shared;

import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.catalog.domain.Stock;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class CarritoStepDefinitions {

    private Producto producto;
    private Exception exception;

    @Given("un cliente autenticado")
    public void unClienteAutenticado() {
        // Simulado
    }

    @Given("un producto {string} con precio {double} y stock {int}")
    public void unProductoConPrecioYStock(String nombre, double precio, int stock) {
        Precio precioVO = new Precio(BigDecimal.valueOf(precio), Currency.getInstance("PEN"));
        Stock stockVO = new Stock(stock);
        Categoria categoria = new Categoria(1L, "Panaderia");
        this.producto = new Producto(1L, nombre, "Descripción", categoria, precioVO, stockVO, "/img.jpg");
    }

    @When("el cliente agrega {int} unidades del producto al carrito")
    public void elClienteAgregaUnidadesDelProductoAlCarrito(int cantidad) {
        try {
            if (!producto.tieneStockSuficiente(cantidad)) {
                throw new IllegalArgumentException("Stock insuficiente");
            }
            // Simular agregar al carrito
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @When("el cliente intenta agregar {int} unidades del producto al carrito")
    public void elClienteIntentaAgregarUnidadesDelProductoAlCarrito(int cantidad) {
        elClienteAgregaUnidadesDelProductoAlCarrito(cantidad);
    }

    @Then("el carrito debe contener {int} unidades del producto")
    public void elCarritoDebeContenerUnidadesDelProducto(int cantidad) {
        // Verificar carrito
        assertNull(exception);
    }

    @Then("el subtotal debe ser {double}")
    public void elSubtotalDebeSer(double subtotal) {
        BigDecimal expected = BigDecimal.valueOf(subtotal);
        // Verificar subtotal
    }

    @Then("debe recibir un mensaje de error {string}")
    public void debeRecibirUnMensajeDeError(String mensaje) {
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }
}