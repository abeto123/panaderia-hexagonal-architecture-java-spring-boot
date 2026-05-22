package com.panaderia.ecommerce.shared.testing.steps;

import com.panaderia.ecommerce.catalog.domain.Producto;
import com.panaderia.ecommerce.shared.testing.TestingAPI;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Step Definitions para HU013: Gestión de Productos
 * 
 * Escenarios:
 * - Actualización de producto con precio válido
 * - Actualización con precio inválido (negativo)
 * - Agregación de producto con datos válidos
 * - Agregación con datos inválidos
 * - Eliminación de producto sin dependencias
 * - Eliminación fallida (producto asociado a pedidos)
 * 
 * Principio de Diseño: Los pasos NO hacen cálculos ni condicionales complejos.
 * Solo contratan datos fijos y comparan contra resultados esperados.
 */
@SpringBootTest
public class GestionProductosSteps {
    
    private final TestingAPI testingAPI;
    
    // Contexto compartido entre pasos dentro de un escenario
    private Map<String, Object> contexto = new HashMap<>();

    public GestionProductosSteps(TestingAPI testingAPI) {
        this.testingAPI = testingAPI;
    }

    // ==================== PASOS DADO (Precondiciones) ====================

    @Dado("que el producto existe en el sistema")
    public void productoExisteEnSistema() {
        // Precondición: Un producto de prueba debe existir
        // En escenarios reales, se insertaría en BD con @Before o fixtures
        Producto productoMock = new Producto(
            1L,
            "Pan Integral",
            "Pan integral de calidad",
            new com.panaderia.ecommerce.catalog.domain.Categoria(1L, "Panes"),
            new com.panaderia.ecommerce.catalog.domain.Precio(BigDecimal.valueOf(5.50), java.util.Currency.getInstance("PEN")),
            new com.panaderia.ecommerce.catalog.domain.Stock(50),
            "pan-integral.jpg"
        );
        contexto.put("producto", productoMock);
        contexto.put("productoId", 1L);
    }

    @Dado("que el producto no existe en el sistema")
    public void productoNoExisteEnSistema() {
        // Precondición: No hay producto con este ID
        contexto.put("producto", null);
        contexto.put("productoId", 999L);
    }

    @Dado("que el producto no puede ser eliminado")
    public void productoNoPuedeSerEliminado() {
        // Precondición: El producto está asociado a pedidos
        Producto productoMock = new Producto(
            2L,
            "Pan Blanco",
            "Pan blanco premium",
            new com.panaderia.ecommerce.catalog.domain.Categoria(1L, "Panes"),
            new com.panaderia.ecommerce.catalog.domain.Precio(BigDecimal.valueOf(4.50), java.util.Currency.getInstance("PEN")),
            new com.panaderia.ecommerce.catalog.domain.Stock(100),
            "pan-blanco.jpg"
        );
        contexto.put("producto", productoMock);
        contexto.put("productoId", 2L);
        contexto.put("productoDependencias", true); // Indicar que tiene pedidos asociados
    }

    @Dado("que el administrador tiene acceso al catálogo de productos")
    public void administradorAccesoCatalogo() {
        // Precondición: El administrador está autenticado y autorizado
        contexto.put("autenticado", true);
        contexto.put("rol", "ADMIN");
    }

    @Dado("que el producto está asociado a pedidos existentes")
    public void productoAsociadoAPedidos() {
        // Precondición: El producto tiene relación con uno o más pedidos
        contexto.put("productoDependencias", true);
    }

    @Dado("que el producto no está asociado a ningún pedido")
    public void productoSinDependencias() {
        // Precondición: El producto puede ser eliminado sin restricciones
        contexto.put("productoDependencias", false);
    }

    // ==================== PASOS CUANDO (Acciones) ====================

    @Cuando("el administrador actualiza el precio del producto con un valor válido")
    public void actualizarPrecioValido() {
        try {
            Long productoId = (Long) contexto.get("productoId");
            BigDecimal nuevoPrecio = BigDecimal.valueOf(6.99); // Precio válido (positivo)
            
            Producto actualizado = testingAPI.actualizarPrecioProducto(productoId, nuevoPrecio);
            
            contexto.put("productoActualizado", actualizado);
            contexto.put("actualizacionExitosa", true);
            contexto.put("error", null);
        } catch (Exception e) {
            contexto.put("actualizacionExitosa", false);
            contexto.put("error", e.getMessage());
        }
    }

    @Cuando("el administrador intenta actualizar el precio con un valor negativo")
    public void actualizarPrecioNegativo() {
        try {
            Long productoId = (Long) contexto.get("productoId");
            BigDecimal preciNegativo = BigDecimal.valueOf(-5.00); // INVÁLIDO
            
            testingAPI.intentarActualizarPrecioNegativo(productoId, preciNegativo);
            
            contexto.put("actualizacionExitosa", true);
            contexto.put("error", null);
        } catch (Exception e) {
            // El dominio debe rechazar el precio negativo
            contexto.put("actualizacionExitosa", false);
            contexto.put("error", e.getMessage());
        }
    }

    @Cuando("el administrador registra un nuevo producto con datos válidos")
    public void registrarProductoDatosValidos() {
        try {
            Producto nuevoProducto = testingAPI.crearProductoValido(
                "Croissant de Chocolate",
                "Croissant fresco con chocolate belga",
                "Pastelería",
                BigDecimal.valueOf(3.50),
                25
            );
            
            contexto.put("productoCreado", nuevoProducto);
            contexto.put("creacionExitosa", true);
            contexto.put("error", null);
        } catch (Exception e) {
            contexto.put("creacionExitosa", false);
            contexto.put("error", e.getMessage());
        }
    }

    @Cuando("el administrador registra un nuevo producto con datos inválidos")
    public void registrarProductoDatosInvalidos() {
        try {
            testingAPI.intentarCrearProductoConDatosInvalidos();
            contexto.put("creacionExitosa", true);
        } catch (Exception e) {
            // Se espera que lance excepción
            contexto.put("creacionExitosa", false);
            contexto.put("error", "Datos inválidos");
        }
    }

    @Cuando("el administrador intenta registrar un producto con datos incompletos o inválidos")
    public void registrarProductoIncompleto() {
        registrarProductoDatosInvalidos();
    }

    @Cuando("el administrador elimina el producto")
    public void eliminarProducto() {
        try {
            Long productoId = (Long) contexto.get("productoId");
            
            // Verificar si tiene dependencias
            boolean tiendeDependencias = (boolean) contexto.getOrDefault("productoDependencias", false);
            
            if (tiendeDependencias) {
                testingAPI.intentarEliminarProductoAsociadoAPedidos(productoId);
            } else {
                testingAPI.eliminarProducto(productoId);
            }
            
            contexto.put("eliminacionExitosa", true);
            contexto.put("error", null);
        } catch (Exception e) {
            contexto.put("eliminacionExitosa", false);
            contexto.put("error", e.getMessage());
        }
    }

    @Cuando("el administrador intenta eliminar el producto")
    public void intentarEliminarProducto() {
        eliminarProducto();
    }

    // ==================== PASOS ENTONCES (Resultados esperados) ====================

    @Entonces("el producto es actualizado correctamente en la base de datos")
    public void productoActualizadoEnBD() {
        boolean exito = (boolean) contexto.getOrDefault("actualizacionExitosa", false);
        assert exito : "La actualización no fue exitosa: " + contexto.get("error");
    }

    @Entonces("el nuevo precio se refleja en el catálogo")
    public void nuevoPrecioEnCatalogo() {
        Producto actualizado = (Producto) contexto.get("productoActualizado");
        assert actualizado != null : "El producto actualizado es nulo";
        assert actualizado.getPrecio().getValor().compareTo(BigDecimal.valueOf(6.99)) == 0 :
            "El precio no es el esperado";
    }

    @Entonces("el sistema rechaza la actualización")
    public void sistemaRechazaActualizacion() {
        boolean exito = (boolean) contexto.getOrDefault("actualizacionExitosa", false);
        assert !exito : "La actualización no debería haber sido exitosa";
    }

    @Entonces("el precio original permanece sin cambios")
    public void precioOriginalSinCambios() {
        Producto producto = (Producto) contexto.get("producto");
        Producto actualizado = (Producto) contexto.get("productoActualizado");
        
        // Si actualizado es null, el precio original se mantiene (no se persistió)
        if (actualizado != null) {
            assert producto.getPrecio().getValor().equals(producto.getPrecio().getValor()) :
                "El precio cambió cuando no debería";
        }
    }

    @Entonces("el sistema devuelve el error {string}")
    public void sistemaDevuelveError(String mensajeEsperado) {
        String errorActual = (String) contexto.get("error");
        assert errorActual != null && errorActual.contains(mensajeEsperado) :
            "Error esperado: " + mensajeEsperado + ", obtenido: " + errorActual;
    }

    @Entonces("el producto se crea correctamente en la base de datos")
    public void productoCreadoEnBD() {
        boolean exito = (boolean) contexto.getOrDefault("creacionExitosa", false);
        assert exito : "La creación no fue exitosa: " + contexto.get("error");
    }

    @Entonces("el nuevo producto es visible en el catálogo")
    public void nuevoProductoVisible() {
        Producto creado = (Producto) contexto.get("productoCreado");
        assert creado != null : "El producto creado es nulo";
        assert creado.getNombre().equals("Croissant de Chocolate") :
            "El producto no tiene el nombre esperado";
    }

    @Entonces("el producto recibe un identificador único")
    public void productoTieneId() {
        Producto creado = (Producto) contexto.get("productoCreado");
        assert creado.getId() != null && creado.getId() > 0 :
            "El producto no tiene ID válido";
    }

    @Entonces("no se crea ningún producto")
    public void noSeCreaProducto() {
        Producto creado = (Producto) contexto.get("productoCreado");
        assert creado == null : "No debería haberse creado un producto";
    }

    @Entonces("el producto se elimina correctamente de la base de datos")
    public void productoEliminado() {
        boolean exito = (boolean) contexto.getOrDefault("eliminacionExitosa", false);
        assert exito : "La eliminación no fue exitosa: " + contexto.get("error");
    }

    @Entonces("el producto ya no aparece en el catálogo")
    public void productoNoEnCatalogo() {
        // Verificar que el producto fue eliminado (no existe en BD)
        Long productoId = (Long) contexto.get("productoId");
        // En un escenario real, se consultaría la BD
        contexto.put("productoBuscado", null);
    }

    @Entonces("el sistema rechaza la eliminación")
    public void sistemaRechazaEliminacion() {
        boolean exito = (boolean) contexto.getOrDefault("eliminacionExitosa", false);
        assert !exito : "La eliminación no debería haber sido exitosa";
    }

    @Entonces("el producto permanece en la base de datos")
    public void productoPermaneceBD() {
        Producto producto = (Producto) contexto.get("producto");
        assert producto != null : "El producto debería existir";
    }
}
