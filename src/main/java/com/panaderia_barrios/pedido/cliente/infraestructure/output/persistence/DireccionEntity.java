package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "direccion")
public class DireccionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDireccion;
    private int idCliente;
    private String alias;
    private String calle;
    private String numero;
    private String referencia;
    private int idDistrito;

    public DireccionEntity() {
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public int getIdCliente() {
        return idCliente;
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

    public String getReferencia() {
        return referencia;
    }

    public int getIdDistrito() {
        return idDistrito;
    }
}