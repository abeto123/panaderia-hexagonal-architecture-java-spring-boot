package com.panaderia_barrios.pedido.envio.domain.ports.input;

public interface CalcularCostoEnvioUseCase {
    double calcular(int idDireccionEntrega, double subtotal);
}