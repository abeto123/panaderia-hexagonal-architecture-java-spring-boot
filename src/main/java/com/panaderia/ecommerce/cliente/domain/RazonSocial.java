package com.panaderia.ecommerce.cliente.domain;

public class RazonSocial {
    private final String valor;

    public RazonSocial(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public boolean isPresent() {
        return valor != null && !valor.trim().isEmpty();
    }

    @Override
    public String toString() {
        return valor;
    }
}