package com.panaderia_barrios.pedido.shared.common;

public enum TipoEntrega {
    DOMICILIO,
    RECOJO;

    public boolean esDomicilio() {
        return this == DOMICILIO;
    }

    public boolean esRecojo() {
        return this == RECOJO;
    }
}