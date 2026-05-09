package com.panaderia_barrios.pedido.catalogo.domain.ports.input;

import java.util.List;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;

public interface ObtenerCatalogoB2CUseCase {
    List<Producto> obtenerCatalogo();
}