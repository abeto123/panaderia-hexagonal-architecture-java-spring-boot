# Laboratorio de Arquitectura Hexagonal con Cucumber BDD
## Vinculación Arquitectónica: Escenarios Gherkin ↔ Casos de Uso

**Proyecto**: E-commerce Panadería Barrios  
**Fecha**: Mayo 22, 2026  
**Asignatura**: Ingeniería de Software II (UNJBG, 2026)  
**Responsables**: Equipo de Desarrollo  

---

## 1. Matriz de Correspondencia Arquitectónica

| # | Escenario Gherkin | Archivo Feature | Puerto Primario / Caso de Uso | Técnica SWEBOK | Datos de Entrada (Válidos) | Datos de Entrada (Inválidos) | Resultado Esperado |
|---|---|---|---|---|---|---|---|
| **HU006** - Emisión de Reportes de Pagos | | | | | | | |
| 1 | Emisión de reporte de pagos con datos exitosa | `reportes-pagos.feature` | `PagoService.generarReporte()` | Caja Negra: Partición de Equivalencia | Sistema con pagos registrados | N/A | Reporte generado con lista de pagos |
| 2 | Intento de emisión de reporte sin pagos registrados | `reportes-pagos.feature` | `PagoService.generarReporte()` | Caja Negra: Análisis de Valores Límite | Sistema sin pagos | N/A | Excepción "No existen pagos" |
| **HU013** - Gestión de Productos | | | | | | | |
| 3 | Actualización de producto con precio válido | `gestion-productos.feature` | `ProductoService.editarProducto()` | Caja Negra: Partición de Equivalencia | Producto existente, precio > 0 | N/A | Producto actualizado en BD |
| 4 | Actualización de producto con precio inválido (negativo) | `gestion-productos.feature` | `ProductoService.editarProducto()` + `Precio.validar()` | Caja Negra: Análisis de Valores Límite | Producto existente, precio < 0 | precio = -5.00 | Rechazo y excepción |
| 5 | Agregación de producto con datos válidos | `gestion-productos.feature` | `ProductoService.crearProducto()` | Caja Negra: Partición de Equivalencia | Todos los campos obligatorios completos | N/A | Producto creado con ID único |
| 6 | Agregación de producto con datos inválidos | `gestion-productos.feature` | `ProductoService.crearProducto()` + Validadores de Dominio | Caja Negra: Análisis de Valores Límite | Campos nulos o vacíos | nombre = null, precio = null | Rechazo con mensaje de error |
| 7 | Eliminación de producto sin dependencias | `gestion-productos.feature` | `ProductoService.eliminarProducto()` | Caja Negra: Partición de Equivalencia | Producto sin asociaciones | N/A | Producto eliminado |
| 8 | Intento de eliminación de producto asociado a pedidos | `gestion-productos.feature` | `ProductoService.eliminarProducto()` + Validador de Dependencias | Caja Negra: Análisis de Valores Límite | Producto con 1+ pedidos | productoDependencias = true | Rechazo con excepción |
| **HU021** - Creación de Pedidos | | | | | | | |
| 9 | Creación de pedido exitosa con stock disponible | `crear-pedidos.feature` | `PedidoService.crearPedido()` | Caja Negra: Partición de Equivalencia | Stock ≥ cantidad, cliente existe | N/A | Pedido creado, stock decrementado |
| 10 | Intento de creación de pedido con stock insuficiente | `crear-pedidos.feature` | `PedidoService.validarStock()` | Caja Negra: Análisis de Valores Límite | Stock < cantidad solicitada | cantSolicitada = 200, stock = 100 | Rechazo y error "Stock insuficiente" |
| 11 | Intento de creación de pedido con productos inexistentes | `crear-pedidos.feature` | `PedidoService.validarProductos()` | Caja Negra: Análisis de Valores Límite | Producto ID inexistente | productoId = 999 | Rechazo y error "Producto inexistente" |

---

## 2. Desglose de Técnicas SWEBOK Aplicadas

### 📌 Caja Negra: Partición de Equivalencia
**Definición** (Washizaki, 2025): Dividir el dominio de entrada en subconjuntos que se comportan de forma equivalente.

**Aplicaciones en este laboratorio**:
- **Escenario 1**: Pagos registrados vs. sin pagos (2 clases de equivalencia)
- **Escenario 3**: Precio válido (positivo y razonable)
- **Escenario 5**: Producto completo con datos requeridos
- **Escenario 9**: Stock suficiente, cliente válido

---

### 📌 Caja Negra: Análisis de Valores Límite
**Definición** (SWEBOK v4.0a): Examinar el comportamiento en los puntos fronterizos de cada partición.

**Aplicaciones en este laboratorio**:
- **Escenario 2**: Límite inferior = 0 pagos (sin registros)
- **Escenario 4**: Precio límite = 0 y < 0 (inválido)
- **Escenario 6**: Valor nulo (límite inferior absoluto)
- **Escenario 8**: Un producto con 1 pedido (mínimo dependencia)
- **Escenario 10**: Stock exactamente insuficiente (200 > 100)
- **Escenario 11**: ID no existente (valor fuera de rango válido)

---

## 3. Mapeo de Pasos Gherkin → Métodos TestingAPI → Servicios

### HU006: Emisión de Reportes de Pagos

```
Escenario: Emisión de reporte de pagos con datos exitosa
├─ Given: que el sistema tiene registrados pagos realizados
│  └─> TestingAPI.contexto.put("pagosExisten", true)
├─ When: el administrador solicita emitir un reporte de pagos
│  └─> TestingAPI.generarReportePagos()
│       └─> PagoService.findAll()
└─ Then: el reporte se genera correctamente
   └─> Assert reportePagos.totalPagos > 0
```

---

### HU013: Gestión de Productos

```
Escenario: Actualización de producto con precio válido
├─ Given: que el producto existe en el sistema
│  └─> TestingAPI.contexto.put("producto", mockProducto)
├─ When: el administrador actualiza el precio con un valor válido
│  └─> TestingAPI.actualizarPrecioProducto(1L, 6.99)
│       └─> ProductoService.editarProducto()
│            └─> Precio.validar() [dominio]
└─ Then: el producto es actualizado correctamente
   └─> Assert producto.getPrecio() == 6.99
```

---

### HU021: Creación de Pedidos

```
Escenario: Creación de pedido exitosa con stock disponible
├─ Given: que exista stock suficiente de los productos
│  └─> TestingAPI.contexto.put("cantidades", [5, 3])
│       TestingAPI.contexto.put("stockInsuficiente", false)
├─ When: el administrador registra un nuevo pedido
│  └─> TestingAPI.crearPedido(1L, [1L, 2L], [5, 3])
│       └─> PedidoService.crearPedido()
│            ├─> validarStock()
│            ├─> decrementarStock()
│            └─> persistirPedido()
└─ Then: el pedido se crea correctamente
   └─> Assert pedido.getId() > 0
```

---

## 4. Estructura de Directorios Entregada

```
ecommerce/
├── src/test/resources/features/
│   ├── reportes-pagos.feature          ← HU006 (2 escenarios)
│   ├── gestion-productos.feature       ← HU013 (6 escenarios)
│   ├── crear-pedidos.feature           ← HU021 (3 escenarios)
│   ├── carrito.feature                 ← (existente)
│   └── horario.feature                 ← (existente)
├── src/test/java/com/panaderia/ecommerce/shared/testing/
│   ├── TestingAPI.java                 ← Puerta de entrada desacoplada
│   └── steps/
│       ├── ReportesPagosSteps.java      ← Implementación HU006
│       ├── GestionProductosSteps.java   ← Implementación HU013
│       └── CrearPedidosSteps.java       ← Implementación HU021
├── src/test/java/com/panaderia/ecommerce/shared/
│   └── CucumberTest.java                ← Configuración actualizada
└── README.md                            ← Este documento
```

---

## 5. Principios de Arquitectura Hexagonal Aplicados

### ✅ Desacoplamiento Total de la UI
- ❌ **Antipatrón**: Selectores CSS, IDs de botones, Thymeleaf directives
- ✅ **Implementado**: `TestingAPI` invoca directamente puertos primarios (Servicios)

### ✅ Gherkin Declarativo (Lenguaje Ubicuo del Negocio)
- ❌ **Antipatrón**: "Cuando el usuario hace clic en #btn-guardar"
- ✅ **Implementado**: "Cuando el administrador registra un nuevo producto"

### ✅ Tests sin Lógica Interna
- ❌ **Antipatrón**: Cálculos de IGV, descuentos, validaciones en tests
- ✅ **Implementado**: Tests planos, solo comparación de datos fijos

### ✅ Inyección de Dependencias
- TestingAPI → ProductoService → ProductoRepository
- TestingAPI → ClienteService → ClienteRepository
- TestingAPI → PedidoService (por implementar)

---

## 6. Certificación de Cumplimiento (Rúbrica UNJBG, 2026)

| Criterio | Evidencia | Estado |
|----------|-----------|--------|
| **Especificaciones Ejecutables** | 3 archivos .feature con 11 escenarios (caminos felices + errores) | ✅ COMPLETO |
| **Tabla de Vinculación** | Este documento con matriz de correspondencia | ✅ COMPLETO |
| **Ejecución Exitosa** | 6+ escenarios GREEN en terminal / CI | ⏳ PENDIENTE (ejecutar `mvn test`) |
| **Desacoplamiento Arquitectónico** | TestingAPI + Step Definitions sin UI | ✅ COMPLETO |
| **Documentación Técnica** | README.md con justificación SWEBOK | ✅ COMPLETO |

---

## 7. Instrucciones para Ejecución y Validación

### 7.1 Ejecutar Tests Localmente

```bash
# Compilar y ejecutar tests Cucumber
mvn clean test -Dtest=CucumberTest

# Generar reportes HTML
# Los reportes se crean en: target/cucumber-reports.html
```

### 7.2 Verificar Requisitos

```bash
# Listar escenarios
mvn test -Dtest=CucumberTest -Dcucumber.options="--dry-run"

# Ejecutar solo HU013
mvn test -Dtest=CucumberTest -Dcucumber.options="--tags @HU013"
```

### 7.3 Interpretar Resultados

```
Feature: Gestión de Productos

  Scenario: Actualización de producto con precio válido
    ✅ PASSED (escenario ejecutó correctamente)

  Scenario: Intento de actualización con precio negativo
    ✅ PASSED (excepción capturada como se esperaba)

6 Scenarios: 6 passed, 0 failed, 0 skipped
11 Steps: 11 passed, 0 failed, 0 skipped
```

---

## 8. Referencias Teóricas

- **Martin, R. C.** (2017, 2018). *Clean Architecture*. Prentice Hall.
  - Principio: Aislamiento de tests de la UI mediante adaptadores.

- **Cockburn, A.** (2005). *Hexagonal Architecture*. Alistair Cockburn's Blog.
  - Principio: Los tests son actores primarios que consumen puertos primarios.

- **Washizaki, H.** (2025). *SWEBOK Guide v4.0a*. IEEE Computer Society.
  - Técnica: Partición de Equivalencia y Análisis de Valores Límite.

- **Cucumber Organization**. (s.f.). *Gherkin Syntax Guide*. cucumber.io
  - Especificación: Sintaxis y prácticas BDD.

---

**Documento generado**: 2026-05-22  
**Próxima revisión**: Post-ejecución de tests
