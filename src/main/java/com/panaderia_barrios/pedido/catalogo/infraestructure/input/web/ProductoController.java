package com.panaderia_barrios.pedido.catalogo.infraestructure.input.web;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;
import com.panaderia_barrios.pedido.catalogo.domain.ports.input.ObtenerCatalogoB2CUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ObtenerCatalogoB2CUseCase obtenerCatalogoB2CUseCase;

    public ProductoController(ObtenerCatalogoB2CUseCase obtenerCatalogoB2CUseCase) {
        this.obtenerCatalogoB2CUseCase = obtenerCatalogoB2CUseCase;
    }

    @GetMapping("/catalogo-b2c")
    public ResponseEntity<List<Producto>> obtenerCatalogoB2C() {
        List<Producto> productos = obtenerCatalogoB2CUseCase.obtenerCatalogo();
        return ResponseEntity.ok(productos);
    }
}