package com.panaderia.ecommerce.catalog.domain;

import java.util.Objects;

public class Categoria {
    private final Long id;
    private final String nombre;

    public Categoria(Long id, String nombre) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "Nombre de categoría es obligatorio");
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id) && Objects.equals(nombre, categoria.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}