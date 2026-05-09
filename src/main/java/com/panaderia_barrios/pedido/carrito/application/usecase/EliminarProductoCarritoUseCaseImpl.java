package com.panaderia_barrios.pedido.carrito.application.usecase;

import com.panaderia_barrios.pedido.carrito.domain.ports.input.EliminarProductoCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.output.CarritoRepository;

import org.springframework.stereotype.Service;

@Service
public class EliminarProductoCarritoUseCaseImpl implements EliminarProductoCarritoUseCase {

    private final CarritoRepository carritoRepository;

    public EliminarProductoCarritoUseCaseImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public boolean eliminar(int clienteId, int productoId) {
        return carritoRepository.eliminarItem(clienteId, productoId);
    }
}