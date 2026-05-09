package com.panaderia_barrios.pedido.pedido.application.usecase;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.CrearPedidoUseCase;
import com.panaderia_barrios.pedido.pedido.domain.ports.output.PedidoRepository;

import org.springframework.stereotype.Service;

@Service
public class CrearPedidoUseCaseImpl implements CrearPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public CrearPedidoUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido crear(Pedido pedido) {
        // Business logic here, e.g., validations, calculations
        // For now, just save
        return pedidoRepository.save(pedido);
    }
}