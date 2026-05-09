package com.panaderia_barrios.pedido.envio.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.envio.domain.ZonaEnvio;
import com.panaderia_barrios.pedido.envio.domain.ports.output.ZonaEnvioRepository;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ZonaEnvioRepositoryAdapter implements ZonaEnvioRepository {

    private final JpaZonaEnvioRepository jpaRepository;

    public ZonaEnvioRepositoryAdapter(JpaZonaEnvioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ZonaEnvio> findByDistritoId(int distritoId) {
        return jpaRepository.findFirstByIdDistrito(distritoId)
                .map(this::toDomain);
    }

    private ZonaEnvio toDomain(ZonaEnvioEntity entity) {
        return new ZonaEnvio(
                entity.getIdZonaEnvio(),
                entity.getIdDistrito(),
                entity.getIdSede(),
                entity.getCostoEnvio(),
                entity.getMontoMinimoGratis()
        );
    }
}