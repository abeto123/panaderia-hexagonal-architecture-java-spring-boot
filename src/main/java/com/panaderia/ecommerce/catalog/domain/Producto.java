package com.panaderia.ecommerce.catalog.domain;

import java.util.Objects;

public class Producto {
    private final Long id;
    private final String nombre;
    private final String descripcion;
    private final Categoria categoria;
    private final Precio precio;
    private final Stock stock;
    private final boolean disponible;
    private final String imagenUrl;

    public Producto(Long id, String nombre, String descripcion, Categoria categoria, Precio precio, Stock stock, String imagenUrl) {
        this(id, nombre, descripcion, categoria, precio, stock, true, imagenUrl);
    }

    public Producto(Long id, String nombre, String descripcion, Categoria categoria, Precio precio, Stock stock, boolean disponible, String imagenUrl) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "Nombre es obligatorio");
        this.descripcion = descripcion;
        this.categoria = Objects.requireNonNull(categoria, "Categoria es obligatoria");
        this.precio = Objects.requireNonNull(precio, "Precio es obligatorio");
        this.stock = Objects.requireNonNull(stock, "Stock es obligatorio");
        this.disponible = disponible;
        this.imagenUrl = imagenUrl;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Precio getPrecio() {
        return precio;
    }

    public Stock getStock() {
        return stock;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public boolean tieneStockSuficiente(int cantidad) {
        return stock.tieneSuficiente(cantidad);
    }

    public Producto disminuirStock(int cantidad) {
        return new Producto(id, nombre, descripcion, categoria, precio, stock.disminuir(cantidad), disponible, imagenUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}