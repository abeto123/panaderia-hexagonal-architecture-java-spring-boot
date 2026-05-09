package com.panaderia_barrios.pedido.envio.infraestructure.output.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zona_envio")
public class ZonaEnvioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idZonaEnvio;
    private int idDistrito;
    private int idSede;
    private double costoEnvio;
    private double montoMinimoGratis;

    public ZonaEnvioEntity() {
    }

    public int getIdZonaEnvio() {
        return idZonaEnvio;
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