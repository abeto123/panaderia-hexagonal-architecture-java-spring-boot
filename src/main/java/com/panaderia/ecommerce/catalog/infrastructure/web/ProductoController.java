package com.panaderia.ecommerce.catalog.infrastructure.web;

import com.panaderia.ecommerce.catalog.application.CategoriaService;
import com.panaderia.ecommerce.catalog.application.ProductoService;
import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/test-categorias")
    @ResponseBody
    @PermitAll
    public java.util.Map<String, Object> testCategorias() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        return java.util.Map.of(
            "total_count", categorias.size(),
            "unique_count", categorias.stream().map(Categoria::getNombre).distinct().count(),
            "categorias", categorias
        );
    }

    @GetMapping
    public String listarProductos(@RequestParam(required = false) Long categoriaId, Model model) {
        List<Producto> productos;
        if (categoriaId != null) {
            Categoria categoria = categoriaService.buscarPorId(categoriaId).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            productos = productoService.listarPorCategoria(categoria);
        } else {
            productos = productoService.listarProductos();
        }
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "cliente/catalogo";
    }
}