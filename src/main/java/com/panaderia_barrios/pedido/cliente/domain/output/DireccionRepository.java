package com.panaderia_barrios.pedido.cliente.domain.output;

import java.util.List;
import java.util.Optional;

import com.panaderia_barrios.pedido.shared.common.Direccion;

public interface DireccionRepository {
    List<Direccion> findByClienteId(int clienteId);
    Optional<Direccion> findById(int id);
}