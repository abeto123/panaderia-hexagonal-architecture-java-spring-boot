package com.panaderia_barrios.pedido.carrito.application.usecase;

import com.panaderia_barrios.pedido.carrito.domain.ports.input.VaciarCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.output.CarritoRepository;

import org.springframework.stereotype.Service;

@Service
public class VaciarCarritoUseCaseImpl implements VaciarCarritoUseCase {

    private final CarritoRepository carritoRepository;

    public VaciarCarritoUseCaseImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public void vaciar(int clienteId) {
        carritoRepository.vaciar(clienteId);
    }
}