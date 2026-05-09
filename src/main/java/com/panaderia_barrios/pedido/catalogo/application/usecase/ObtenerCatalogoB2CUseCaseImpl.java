package com.panaderia_barrios.pedido.catalogo.application.usecase;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;
import com.panaderia_barrios.pedido.catalogo.domain.ports.input.ObtenerCatalogoB2CUseCase;
import com.panaderia_barrios.pedido.catalogo.domain.ports.output.ProductoRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ObtenerCatalogoB2CUseCaseImpl implements ObtenerCatalogoB2CUseCase {

    private final ProductoRepository productoRepository;

    public ObtenerCatalogoB2CUseCaseImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> obtenerCatalogo() {
        return productoRepository.findCatalogoB2C();
    }
}