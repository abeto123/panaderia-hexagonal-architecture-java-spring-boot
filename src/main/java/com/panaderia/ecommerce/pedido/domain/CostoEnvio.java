package com.panaderia.ecommerce.pedido.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class CostoEnvio {
    private final BigDecimal valor;
    private final Currency moneda;

    public CostoEnvio(BigDecimal valor, Currency moneda) {
        this.valor = valor != null ? valor : BigDecimal.ZERO;
        this.moneda = moneda != null ? moneda : Currency.getInstance("PEN");
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Currency getMoneda() {
        return moneda;
    }

    public boolean esGratis() {
        return valor.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public String toString() {
        return valor + " " + moneda.getCurrencyCode();
    }
}