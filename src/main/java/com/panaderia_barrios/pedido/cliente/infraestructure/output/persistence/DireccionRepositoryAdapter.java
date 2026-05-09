package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import com.panaderia_barrios.pedido.cliente.domain.output.DireccionRepository;
import com.panaderia_barrios.pedido.shared.common.Direccion;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DireccionRepositoryAdapter implements DireccionRepository {

    private final JpaDireccionRepository jpaRepository;

    public DireccionRepositoryAdapter(JpaDireccionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Direccion> findByClienteId(int clienteId) {
        return jpaRepository.findByIdCliente(clienteId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Direccion> findById(int id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Direccion toDomain(DireccionEntity entity) {
        return new Direccion(
                entity.getIdDireccion(),
                entity.getIdCliente(),
                entity.getAlias(),
                entity.getCalle(),
                entity.getNumero(),
                entity.getReferencia(),
                entity.getIdDistrito(),
                null
        );
    }
}