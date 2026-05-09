package com.panaderia_barrios.pedido.envio.infraestructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaZonaEnvioRepository extends JpaRepository<ZonaEnvioEntity, Integer> {
    Optional<ZonaEnvioEntity> findFirstByIdDistrito(int idDistrito);
}