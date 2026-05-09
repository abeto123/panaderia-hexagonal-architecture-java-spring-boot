package com.panaderia_barrios.pedido.envio.domain;

public class ZonaEnvio {
    private final int id;
    private final int idDistrito;
    private final int idSede;
    private final double costoEnvio;
    private final double montoMinimoGratis;

    public ZonaEnvio(int id, int idDistrito, int idSede, double costoEnvio, double montoMinimoGratis) {
        this.id = id;
        this.idDistrito = idDistrito;
        this.idSede = idSede;
        this.costoEnvio = costoEnvio;
        this.montoMinimoGratis = montoMinimoGratis;
    }

    public int getId() {
        return id;
    }

    public int getIdDistrito() {
        return idDistrito;
    }

    public int getIdSede() {
        return idSede;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public double getMontoMinimoGratis() {
        return montoMinimoGratis;
    }
}