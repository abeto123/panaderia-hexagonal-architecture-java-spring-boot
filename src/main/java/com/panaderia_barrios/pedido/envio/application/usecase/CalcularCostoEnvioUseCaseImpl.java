package com.panaderia_barrios.pedido.envio.application.usecase;

import com.panaderia_barrios.pedido.cliente.domain.output.DireccionRepository;
import com.panaderia_barrios.pedido.envio.domain.ports.input.CalcularCostoEnvioUseCase;
import com.panaderia_barrios.pedido.envio.domain.ports.output.ZonaEnvioRepository;

import org.springframework.stereotype.Service;

@Service
public class CalcularCostoEnvioUseCaseImpl implements CalcularCostoEnvioUseCase {

    private final DireccionRepository direccionRepository;
    private final ZonaEnvioRepository zonaEnvioRepository;

    public CalcularCostoEnvioUseCaseImpl(DireccionRepository direccionRepository,
                                         ZonaEnvioRepository zonaEnvioRepository) {
        this.direccionRepository = direccionRepository;
        this.zonaEnvioRepository = zonaEnvioRepository;
    }

    @Override
    public double calcular(int idDireccionEntrega, double subtotal) {
        return direccionRepository.findById(idDireccionEntrega)
                .flatMap(direccion -> zonaEnvioRepository.findByDistritoId(direccion.getDistritoId()))
                .map(zonaEnvio -> {
                    if (zonaEnvio.getMontoMinimoGratis() > 0 && subtotal >= zonaEnvio.getMontoMinimoGratis()) {
                        return 0.0;
                    }
                    return zonaEnvio.getCostoEnvio();
                })
                .orElse(0.0);
    }
}