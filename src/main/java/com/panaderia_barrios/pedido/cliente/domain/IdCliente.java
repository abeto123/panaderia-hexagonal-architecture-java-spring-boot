package com.panaderia_barrios.pedido.cliente.domain;

public class IdCliente {
    private final int id;

    public IdCliente(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IdCliente idCliente = (IdCliente) obj;
        return id == idCliente.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "IdCliente{" + "id=" + id + '}';
    }
}