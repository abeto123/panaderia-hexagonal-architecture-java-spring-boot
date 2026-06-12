package com.panaderia.ecommerce.catalog.infrastructure.web;

import com.panaderia.ecommerce.catalog.application.CategoriaService;
import com.panaderia.ecommerce.catalog.application.ProductoService;
import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.catalog.domain.Stock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping({"/admin/productos", "/admin/gestionProductos"})
public class ProductoAdminController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final JdbcTemplate jdbcTemplate;

    public ProductoAdminController(ProductoService productoService, CategoriaService categoriaService, JdbcTemplate jdbcTemplate) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public String listarProductos(Model model) {
        List<ProductoAdminView> productos = jdbcTemplate.query(
                "SELECT p.id_producto, p.nombre, p.precio, p.stock_minimo AS stock, p.disponible, p.foto, c.nombre AS categoria " +
                        "FROM producto p " +
                        "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                        "ORDER BY p.id_producto",
                (rs, rowNum) -> new ProductoAdminView(
                        rs.getLong("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getBoolean("disponible"),
                        rs.getString("foto")
                )
        );
        model.addAttribute("productos", productos);
        return "admin/productos";
    }

    @GetMapping("/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/producto-form";
    }

    @PostMapping
    public String crearProducto(@RequestParam String nombre,
                               @RequestParam String descripcion,
                               @RequestParam Long categoriaId,
                               @RequestParam BigDecimal precio,
                               @RequestParam Integer stock,
                               @RequestParam(required = false) MultipartFile imagen,
                               @RequestParam(required = false, defaultValue = "false") Boolean disponible,
                               RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.buscarPorId(categoriaId).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            Precio precioVO = new Precio(precio, Currency.getInstance("PEN"));
            Stock stockVO = new Stock(stock);
            
            String imagenUrl = "";
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = guardarImagen(imagen);
            }
            
            productoService.crearProducto(nombre, descripcion, categoria, precioVO, stockVO, disponible != null && disponible, imagenUrl);
            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear producto: " + e.getMessage());
        }
        return "redirect:/admin/gestionProductos";
    }

    @GetMapping({"/{id}/editar", "/{id}"})
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerProducto(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/producto-form";
    }

    @PostMapping({"/{id}", "/{id}/"})
    public String actualizarProducto(@PathVariable Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String descripcion,
                                    @RequestParam Long categoriaId,
                                    @RequestParam BigDecimal precio,
                                    @RequestParam Integer stock,
                                    @RequestParam(required = false) MultipartFile imagen,
                                    @RequestParam(required = false, defaultValue = "false") Boolean disponible,
                                    RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.buscarPorId(categoriaId).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            Precio precioVO = new Precio(precio, Currency.getInstance("PEN"));
            Stock stockVO = new Stock(stock);
            
            Producto productoActual = productoService.obtenerProducto(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            String imagenUrl = productoActual.getImagenUrl();
            
            if (imagen != null && !imagen.isEmpty()) {
                imagenUrl = guardarImagen(imagen);
            }
            
            productoService.editarProducto(id, nombre, descripcion, categoria, precioVO, stockVO, disponible != null && disponible, imagenUrl);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar producto: " + e.getMessage());
        }
        return "redirect:/admin/gestionProductos";
    }

    @PostMapping({"/{id}/eliminar", "/{id}/eliminar/"})
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/admin/gestionProductos";
    }

    private String guardarImagen(MultipartFile archivo) throws IOException {
        // Crear directorio si no existe
        Path uploadDir = Paths.get("src/main/resources/static/images/productos");
        Files.createDirectories(uploadDir);
        
        // Generar nombre único para el archivo
        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = uploadDir.resolve(nombreArchivo);
        
        // Guardar archivo
        Files.write(rutaArchivo, archivo.getBytes());
        
        // Retornar ruta relativa para la BD
        return "/images/productos/" + nombreArchivo;
    }

    private static record ProductoAdminView(Long id, String nombre, String categoria, BigDecimal precio, Integer stock, Boolean disponible, String imagenUrl) {
    }
}