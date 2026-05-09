package com.panaderia_barrios.pedido.carrito.application.usecase;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.CarritoItem;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.AgregarProductoCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.output.CarritoRepository;

import org.springframework.stereotype.Service;

@Service
public class AgregarProductoCarritoUseCaseImpl implements AgregarProductoCarritoUseCase {

    private final CarritoRepository carritoRepository;

    public AgregarProductoCarritoUseCaseImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public Carrito agregar(int clienteId, CarritoItem item) {
        return carritoRepository.agregarOActualizarItem(clienteId, item);
    }
}