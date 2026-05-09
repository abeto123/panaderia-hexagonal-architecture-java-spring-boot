package com.panaderia_barrios.pedido.carrito.application.usecase;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.ObtenerCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.output.CarritoRepository;

import org.springframework.stereotype.Service;

@Service
public class ObtenerCarritoUseCaseImpl implements ObtenerCarritoUseCase {

    private final CarritoRepository carritoRepository;

    public ObtenerCarritoUseCaseImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public Carrito obtener(int clienteId) {
        return carritoRepository.obtenerPorClienteId(clienteId);
    }
}