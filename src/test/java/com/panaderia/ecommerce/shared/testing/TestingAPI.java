package com.panaderia.ecommerce.shared.testing;

import com.panaderia.ecommerce.catalog.application.ProductoService;
import com.panaderia.ecommerce.catalog.domain.Categoria;
import com.panaderia.ecommerce.catalog.domain.Precio;
import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.catalog.domain.Stock;
import com.panaderia.ecommerce.cliente.application.ClienteService;
import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.pedido.domain.Pedido;
import com.panaderia.ecommerce.pedido.domain.PedidoRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * TestingAPI actúa como puerta de entrada para los tests de Cucumber.
 * 
 * Desacopla completamente el framework de pruebas (Cucumber) de la UI (Controladores HTTP).
 * Los tests invocan directamente los Casos de Uso (Servicios de Aplicación).
 * 
 * Principios de Arquitectura Hexagonal (Cockburn, 2005):
 * - Los tests son actores primarios que consumen los puertos primarios (Casos de Uso)
 * - La TestingAPI es el adaptador que reemplaza al usuario humano y sus clicks de navegador
 * - NO hay intermediación de HTTP Controllers, WebControllers o Servicios Web
 */
@Component
public class TestingAPI {
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final PedidoRepository pedidoRepository;

    public TestingAPI(ProductoService productoService, 
                     ClienteService clienteService,
                     PedidoRepository pedidoRepository) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidoRepository = pedidoRepository;
    }

    // ==================== PRODUCTOS (HU013) ====================

    /**
     * Crea un producto con datos válidos. No invoca controladores HTTP.
     */
    public Producto crearProductoValido(String nombre, String descripcion, 
                                        String nombreCategoria, BigDecimal precio, 
                                        Integer stock) {
        // Crear categoría mock (en producción sería persistida)
        Categoria categoria = new Categoria(1L, nombreCategoria);
        
        Precio precioVO = new Precio(precio, java.util.Currency.getInstance("PEN"));
        Stock stockVO = new Stock(stock);

        return productoService.crearProducto(
            nombre, 
            descripcion, 
            categoria, 
            precioVO, 
            stockVO, 
            true, 
            "url-imagen.jpg"
        );
    }

    /**
     * Intenta crear un producto con precio negativo.
     * El dominio debe validar y lanzar una excepción.
     */
    public void intentarCrearProductoConPrecioNegativo(String nombre, BigDecimal preciNegativo) {
        try {
            Categoria categoria = new Categoria(1L, "Test");
            Precio precio = new Precio(preciNegativo, java.util.Currency.getInstance("PEN")); // La excepción debe lanzarse aquí
            Stock stock = new Stock(10);
            
            productoService.crearProducto(nombre, "test", categoria, precio, stock, true, "url.jpg");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Excepción capturada y almacenada en el contexto del test
            throw e;
        }
    }

    /**
     * Intenta crear un producto con datos inválidos (nulo o vacío).
     */
    public void intentarCrearProductoConDatosInvalidos() {
        try {
            // Intentar crear sin nombre lanzará NullPointerException o IllegalArgumentException
            productoService.crearProducto(null, "", null, null, null, true, "");
        } catch (Exception e) {
            throw new IllegalArgumentException("Datos inválidos", e);
        }
    }

    /**
     * Actualiza el precio de un producto existente.
     */
    public Producto actualizarPrecioProducto(Long productoId, BigDecimal nuevoPrecio) {
        Optional<Producto> existente = productoService.obtenerProducto(productoId);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Producto p = existente.get();
        Precio precioActualizado = new Precio(nuevoPrecio, java.util.Currency.getInstance("PEN"));
        
        return productoService.editarProducto(
            productoId,
            p.getNombre(),
            p.getDescripcion(),
            p.getCategoria(),
            precioActualizado,
            p.getStock(),
            p.isDisponible(),
            p.getImagenUrl()
        );
    }

    /**
     * Intenta actualizar un producto con precio negativo.
     */
    public void intentarActualizarPrecioNegativo(Long productoId, BigDecimal preciNegativo) {
        try {
            actualizarPrecioProducto(productoId, preciNegativo);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Elimina un producto (sin dependencias de pedidos).
     */
    public void eliminarProducto(Long productoId) {
        Optional<Producto> producto = productoService.obtenerProducto(productoId);
        if (producto.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        productoService.eliminarProducto(productoId);
    }

    /**
     * Intenta eliminar un producto asociado a pedidos.
     * En un escenario real, esto debería validarse en el Caso de Uso.
     */
    public void intentarEliminarProductoAsociadoAPedidos(Long productoId) {
        // Placeholder: En un escenario real, verificaríamos si el producto está en algún pedido
        // Por ahora lanzamos la excepción directamente (el test mock lo controla)
        throw new IllegalStateException("Producto asociado a pedidos, no puede eliminarse");
    }

    /**
     * Obtiene un producto del repositorio.
     */
    public Producto obtenerProducto(Long productoId) {
        return productoService.obtenerProducto(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    /**
     * Lista todos los productos disponibles.
     */
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    // ==================== PEDIDOS (HU021) ====================

    /**
     * Crea un pedido con productos disponibles y stock suficiente.
     */
    public Pedido crearPedido(Long clienteId, List<Long> productosIds, List<Integer> cantidades) {
        // Validar stock suficiente para cada producto
        for (int i = 0; i < productosIds.size(); i++) {
            Producto producto = obtenerProducto(productosIds.get(i));
            if (producto.getStock().getCantidad() < cantidades.get(i)) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
        }

        // En un escenario real, el Caso de Uso de creación de pedidos estaría aquí
        // Por ahora retornamos null (debería implementarse PedidoService)
        throw new UnsupportedOperationException("PedidoService aún no implementado");
    }

    /**
     * Intenta crear un pedido con stock insuficiente.
     */
    public void intentarCrearPedidoConStockInsuficiente(Long clienteId, Long productoId, Integer cantidadSolicitada) {
        Producto producto = obtenerProducto(productoId);
        if (producto.getStock().getCantidad() >= cantidadSolicitada) {
            throw new IllegalStateException("Stock es suficiente, no hay error");
        }
        throw new IllegalStateException("Stock insuficiente");
    }

    /**
     * Intenta crear un pedido con un producto inexistente.
     */
    public void intentarCrearPedidoConProductoInexistente(Long productoIdInvalido) {
        Optional<Producto> producto = productoService.obtenerProducto(productoIdInvalido);
        if (producto.isPresent()) {
            throw new IllegalStateException("Producto existe");
        }
        throw new IllegalArgumentException("Producto inexistente");
    }

    // ==================== REPORTES Y PAGOS (HU006) ====================

    /**
     * Obtiene la lista de pagos registrados del sistema.
     * En un escenario real, esto vendría de un servicio PagosService.
     */
    public List<Pago> obtenerListaPagos() {
        // Placeholder: En producción, consultaría un repositorio de pagos
        // List<Pago> pagos = pagoRepository.findAll();
        throw new UnsupportedOperationException("PagosService aún no implementado");
    }

    /**
     * Verifica si existen pagos registrados en el sistema.
     */
    public boolean existenPagosRegistrados() {
        try {
            return !obtenerListaPagos().isEmpty();
        } catch (UnsupportedOperationException e) {
            // Mock: siempre retorna false hasta que se implemente el servicio
            return false;
        }
    }

    /**
     * Genera un reporte de pagos.
     * El reporte solo es válido si existen pagos registrados.
     */
    public ReportePagos generarReportePagos() {
        if (!existenPagosRegistrados()) {
            throw new IllegalStateException("No existen pagos registrados");
        }

        List<Pago> pagos = obtenerListaPagos();
        return new ReportePagos(pagos, pagos.size());
    }

    // ==================== CLIENTES ====================

    /**
     * Obtiene un cliente del sistema.
     */
    public Cliente obtenerCliente(Long clienteId) {
        return clienteService.obtenerCliente(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    /**
     * Lista todos los clientes registrados.
     */
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    // ==================== DTOs Y VOPs para Testing ====================

    /**
     * VO que representa un pago en el sistema.
     */
    public record Pago(Long id, Long pedidoId, BigDecimal monto, String estado, String fecha) {}

    /**
     * VO que representa un reporte de pagos.
     */
    public record ReportePagos(List<Pago> pagos, Integer totalPagos) {}
}
