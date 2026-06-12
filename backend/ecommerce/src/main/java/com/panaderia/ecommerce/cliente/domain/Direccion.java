package com.panaderia.ecommerce.cliente.domain;

public class Direccion {
    private final String alias;
    private final String calle;
    private final String numero;
    private final String distrito;
    private final String referencia;

    public Direccion(String alias, String calle, String numero, String distrito, String referencia) {
        this.alias = alias != null ? alias : "";
        this.calle = calle;
        this.numero = numero;
        this.distrito = distrito;
        this.referencia = referencia;
    }

    public String getAlias() {
        return alias;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumero() {
        return numero;
    }

    public String getDistrito() {
        return distrito;
    }

    public String getReferencia() {
        return referencia;
    }

    @Override
    public String toString() {
        return calle + " " + numero + ", " + distrito + (referencia != null ? " (" + referencia + ")" : "");
    }
}