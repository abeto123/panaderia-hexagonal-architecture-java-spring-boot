package com.panaderia.ecommerce.cliente.domain;

public class Ruc {
    private final String valor;

    public Ruc(String valor) {
        if (valor != null && !valor.matches("\\d{11}")) {
            throw new IllegalArgumentException("RUC debe tener 11 dígitos");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public boolean isPresent() {
        return valor != null;
    }

    @Override
    public String toString() {
        return valor;
    }
}