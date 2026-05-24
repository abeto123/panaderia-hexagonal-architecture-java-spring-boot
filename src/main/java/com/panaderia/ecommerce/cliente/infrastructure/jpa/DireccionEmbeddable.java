package com.panaderia.ecommerce.cliente.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DireccionEmbeddable {
    @Column(name = "alias")
    private String alias;
    @Column(name = "calle")
    private String calle;
    @Column(name = "numero")
    private String numero;
    @Column(name = "distrito")
    private String distrito;
    @Column(name = "referencia")
    private String referencia;

    public DireccionEmbeddable() {
    }

    public DireccionEmbeddable(String alias, String calle, String numero, String distrito, String referencia) {
        this.alias = alias;
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
}
