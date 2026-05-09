package com.panaderia_barrios.pedido.shared.common;

public class Direccion {
    private final int id;
    private final int clienteId;
    private final String alias;
    private final String calle;
    private final String numero;
    private final String referencia;
    private final int distritoId;
    private final String distritoNombre;

    public Direccion(int id, int clienteId, String alias, String calle, String numero, String referencia, int distritoId, String distritoNombre) {
        this.id = id;
        this.clienteId = clienteId;
        this.alias = alias;
        this.calle = calle;
        this.numero = numero;
        this.referencia = referencia;
        this.distritoId = distritoId;
        this.distritoNombre = distritoNombre;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
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

    public int getDistritoId() {
        return distritoId;
    }

    public String getDistritoNombre() {
        return distritoNombre;
    }
}