package com.panaderia.ecommerce.catalog.application;

import com.panaderia.ecommerce.catalog.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProducto(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(String nombre, String descripcion, Categoria categoria, Precio precio, Stock stock, boolean disponible, String imagenUrl) {
        Producto producto = new Producto(null, nombre, descripcion, categoria, precio, stock, disponible, imagenUrl);
        return productoRepository.save(producto);
    }

    public Producto editarProducto(Long id, String nombre, String descripcion, Categoria categoria, Precio precio, Stock stock, boolean disponible, String imagenUrl) {
        Optional<Producto> existente = productoRepository.findById(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        Producto producto = new Producto(id, nombre, descripcion, categoria, precio, stock, disponible, imagenUrl);
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> listarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }
}