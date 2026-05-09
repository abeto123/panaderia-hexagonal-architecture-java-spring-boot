package com.panaderia_barrios.pedido.envio.domain.ports.output;

import java.util.Optional;

import com.panaderia_barrios.pedido.envio.domain.ZonaEnvio;

public interface ZonaEnvioRepository {
    Optional<ZonaEnvio> findByDistritoId(int distritoId);
}