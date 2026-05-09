package com.panaderia_barrios.pedido.pedido.application.usecase;

import com.panaderia_barrios.pedido.pedido.domain.Pedido;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.ObtenerHistorialPedidosUseCase;
import com.panaderia_barrios.pedido.pedido.domain.ports.output.PedidoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObtenerHistorialPedidosUseCaseImpl implements ObtenerHistorialPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    public ObtenerHistorialPedidosUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> obtenerPorCliente(int clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }
}