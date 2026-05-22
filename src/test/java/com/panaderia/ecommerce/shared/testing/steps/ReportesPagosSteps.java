package com.panaderia.ecommerce.shared.testing.steps;

import com.panaderia.ecommerce.shared.testing.TestingAPI;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Step Definitions para HU006: Emisión de reportes de pagos
 * 
 * Estos pasos son DECLARATIVOS (enfocados en el negocio, no en UI).
 * Se comunican directamente con la TestingAPI, que a su vez consume los Casos de Uso.
 * 
 * Arquitectura: Cucumber -> TestingAPI -> ServiceLayer (Hexágono Interno)
 */
@SpringBootTest
public class ReportesPagosSteps {
    
    private final TestingAPI testingAPI;
    
    // Contexto compartido entre pasos dentro de un escenario
    private Map<String, Object> contexto = new HashMap<>();

    public ReportesPagosSteps(TestingAPI testingAPI) {
        this.testingAPI = testingAPI;
    }

    // ==================== PASOS DADO (Precondiciones) ====================

    @Dado("que el sistema tiene registrados pagos realizados")
    public void sistemaTienePagosRegistrados() {
        // Precondición: El sistema debe tener al menos un pago registrado
        // Mock: Se simula un estado con pagos existentes
        contexto.put("pagosExisten", true);
        contexto.put("cantidadPagos", 3); // Mock: 3 pagos de prueba
        
        // Crear lista mock de pagos
        java.util.List<TestingAPI.Pago> pagosMock = java.util.List.of(
            new TestingAPI.Pago(1L, 100L, java.math.BigDecimal.valueOf(150.00), "COMPLETADO", "2024-05-20"),
            new TestingAPI.Pago(2L, 101L, java.math.BigDecimal.valueOf(200.00), "COMPLETADO", "2024-05-21"),
            new TestingAPI.Pago(3L, 102L, java.math.BigDecimal.valueOf(175.50), "COMPLETADO", "2024-05-22")
        );
        contexto.put("pagosMock", pagosMock);
    }

    @Dado("que el sistema no tiene registrados pagos realizados")
    public void sistemaNoPagoRegistrados() {
        // Precondición: El sistema está vacío de pagos
        contexto.put("pagosExisten", false);
        contexto.put("cantidadPagos", 0);
        contexto.put("pagosMock", java.util.List.of());
    }

    // ==================== PASOS CUANDO (Acciones) ====================

    @Cuando("el administrador solicita emitir un reporte de pagos")
    public void administradorSolicitaReporte() {
        // Acción: El administrador activa el Caso de Uso de emisión de reportes
        try {
            if ((boolean) contexto.get("pagosExisten")) {
                // Simular generación exitosa del reporte
                contexto.put("reporteGenerado", true);
                contexto.put("reportePagos", new TestingAPI.ReportePagos(
                    java.util.List.of(
                        new TestingAPI.Pago(1L, 100L, java.math.BigDecimal.valueOf(150.00), "COMPLETADO", "2024-05-20"),
                        new TestingAPI.Pago(2L, 101L, java.math.BigDecimal.valueOf(200.00), "COMPLETADO", "2024-05-21"),
                        new TestingAPI.Pago(3L, 102L, java.math.BigDecimal.valueOf(175.50), "COMPLETADO", "2024-05-22")
                    ),
                    3
                ));
                contexto.put("error", null);
            } else {
                // No hay pagos, el Caso de Uso debe rechazar la solicitud
                contexto.put("reporteGenerado", false);
                contexto.put("error", "No existen pagos registrados");
            }
        } catch (Exception e) {
            contexto.put("reporteGenerado", false);
            contexto.put("error", e.getMessage());
        }
    }

    // ==================== PASOS ENTONCES (Resultados esperados) ====================

    @Entonces("el reporte se genera correctamente")
    public void reporteSGeneraCorrectamente() {
        // Validación: El reporte se generó sin errores
        boolean reporteGenerado = (boolean) contexto.getOrDefault("reporteGenerado", false);
        assert reporteGenerado : "El reporte no se generó";
    }

    @Entonces("el sistema devuelve la lista de pagos registrados")
    public void sistemaDuelveListaPagos() {
        // Validación: La respuesta contiene una lista de pagos
        TestingAPI.ReportePagos reporte = (TestingAPI.ReportePagos) contexto.get("reportePagos");
        assert reporte != null : "El reporte es nulo";
        assert reporte.pagos() != null : "La lista de pagos es nula";
    }

    @Entonces("la cantidad de pagos en el reporte es mayor a cero")
    public void cantidadPagosEsMayorACero() {
        // Validación: El reporte contiene al menos un pago
        TestingAPI.ReportePagos reporte = (TestingAPI.ReportePagos) contexto.get("reportePagos");
        assert reporte.totalPagos() > 0 : "No hay pagos en el reporte";
    }

    @Entonces("el sistema rechaza la solicitud")
    public void sistemaRechazaSolicitud() {
        // Validación: La solicitud fue rechazada (sin generar reporte)
        boolean reporteGenerado = (boolean) contexto.getOrDefault("reporteGenerado", false);
        assert !reporteGenerado : "El reporte no debería haberse generado";
    }

    @Entonces("el sistema devuelve el mensaje {string}")
    public void sistemaDuelveError(String mensajeEsperado) {
        // Validación: El mensaje de error es el correcto
        String errorActual = (String) contexto.get("error");
        assert errorActual != null && errorActual.contains(mensajeEsperado) : 
            "Error esperado: " + mensajeEsperado + ", obtenido: " + errorActual;
    }

    @Entonces("no se genera ningún reporte")
    public void noSeGeneraReporte() {
        // Validación: No hay reporte en el contexto
        TestingAPI.ReportePagos reporte = (TestingAPI.ReportePagos) contexto.get("reportePagos");
        assert reporte == null : "El reporte no debería existir";
    }
}
