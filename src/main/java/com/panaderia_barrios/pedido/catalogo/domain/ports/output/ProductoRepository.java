package com.panaderia_barrios.pedido.catalogo.domain.ports.output;

import java.util.List;
import java.util.Optional;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;

public interface ProductoRepository {
    List<Producto> findCatalogoB2C();
    List<Producto> findCatalogoB2B();
    Optional<Producto> findById(int id);
    Producto save(Producto producto);
    void deleteById(int id);
}