package com.panaderia_barrios.pedido.pedido.application.usecase;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.ObtenerDetallePedidoUseCase;
import com.panaderia_barrios.pedido.pedido.domain.ports.output.PedidoRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObtenerDetallePedidoUseCaseImpl implements ObtenerDetallePedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public ObtenerDetallePedidoUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Optional<Pedido> obtenerPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId);
    }
}