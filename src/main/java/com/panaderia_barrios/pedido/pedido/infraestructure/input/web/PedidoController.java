package com.panaderia_barrios.pedido.pedido.infraestructure.input.web;

import com.panaderia_barrios.pedido.catalogo.domain.Producto;
import com.panaderia_barrios.pedido.catalogo.domain.ports.output.ProductoRepository;
import com.panaderia_barrios.pedido.cliente.domain.IdCliente;
import com.panaderia_barrios.pedido.envio.domain.ports.input.CalcularCostoEnvioUseCase;
import com.panaderia_barrios.pedido.pedido.domain.Pedido;
import com.panaderia_barrios.pedido.pedido.domain.PedidoItem;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.CrearPedidoUseCase;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.ObtenerDetallePedidoUseCase;
import com.panaderia_barrios.pedido.pedido.domain.ports.input.ObtenerHistorialPedidosUseCase;
import com.panaderia_barrios.pedido.shared.common.Dinero;
import com.panaderia_barrios.pedido.shared.common.FechaEntrega;
import com.panaderia_barrios.pedido.shared.common.TipoEntrega;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final CalcularCostoEnvioUseCase calcularCostoEnvioUseCase;
    private final ObtenerHistorialPedidosUseCase historialPedidosUseCase;
    private final ObtenerDetallePedidoUseCase detallePedidoUseCase;
    private final ProductoRepository productoRepository;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase,
                            CalcularCostoEnvioUseCase calcularCostoEnvioUseCase,
                            ObtenerHistorialPedidosUseCase historialPedidosUseCase,
                            ObtenerDetallePedidoUseCase detallePedidoUseCase,
                            ProductoRepository productoRepository) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.calcularCostoEnvioUseCase = calcularCostoEnvioUseCase;
        this.historialPedidosUseCase = historialPedidosUseCase;
        this.detallePedidoUseCase = detallePedidoUseCase;
        this.productoRepository = productoRepository;
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody CrearPedidoRequest request) {
        IdCliente idCliente = new IdCliente(request.getIdCliente());
        FechaEntrega fechaEntrega = new FechaEntrega(request.getFechaEntrega());
        TipoEntrega tipoEntrega = TipoEntrega.valueOf(request.getTipoEntrega().toUpperCase());
        List<PedidoItem> items = request.getItems().stream()
                .map(this::mapToPedidoItem)
                .collect(Collectors.toList());

        double subtotal = items.stream().mapToDouble(PedidoItem::getSubtotal).sum();
        double costoEnvio = calcularCostoEnvioUseCase.calcular(request.getIdDireccionEntrega(), subtotal);

        Pedido pedido = new Pedido(
                null,
                idCliente,
                request.getIdSede(),
                request.getIdDireccionEntrega(),
                tipoEntrega,
                fechaEntrega,
                request.getVentanaEntrega(),
                new Dinero(subtotal),
                new Dinero(costoEnvio),
                "PENDIENTE_PAGO",
                request.getTipoComprobante(),
                items
        );

        Pedido creado = crearPedidoUseCase.crear(pedido);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerHistorial(@PathVariable int clienteId) {
        List<Pedido> pedidos = historialPedidosUseCase.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerDetalle(@PathVariable Long pedidoId) {
        Optional<Pedido> pedido = detallePedidoUseCase.obtenerPorId(pedidoId);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/recalcular")
    public ResponseEntity<CalcularTotalesResponse> recalcularTotales(@RequestBody RecalcularTotalesRequest request) {
        double subtotal = request.getItems().stream()
                .mapToDouble(item -> mapToPedidoItem(item).getSubtotal())
                .sum();
        double costoEnvio = calcularCostoEnvioUseCase.calcular(request.getIdDireccionEntrega(), subtotal);
        return ResponseEntity.ok(new CalcularTotalesResponse(subtotal, costoEnvio, subtotal + costoEnvio));
    }

    private PedidoItem mapToPedidoItem(ItemRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.getProductoId()));
        double precioUnitario = producto.getPrecioB2c();
        if (request.getMontoSolicitadoEntero() != null && request.getMontoSolicitadoEntero() > 0 && producto.getUnidadesBaseB2b() > 0) {
            precioUnitario = producto.getSolesBaseB2b() / producto.getUnidadesBaseB2b();
        }
        double subtotal = precioUnitario * request.getCantidad();
        return new PedidoItem(request.getProductoId(), request.getCantidad(), request.getMontoSolicitadoEntero(), precioUnitario, subtotal);
    }

    public static class CrearPedidoRequest {
        private int idCliente;
        private Integer idSede;
        private int idDireccionEntrega;
        private String tipoEntrega;
        private String fechaEntrega;
        private String ventanaEntrega;
        private String tipoComprobante;
        private java.util.List<ItemRequest> items;

        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
        public Integer getIdSede() { return idSede; }
        public void setIdSede(Integer idSede) { this.idSede = idSede; }
        public int getIdDireccionEntrega() { return idDireccionEntrega; }
        public void setIdDireccionEntrega(int idDireccionEntrega) { this.idDireccionEntrega = idDireccionEntrega; }
        public String getTipoEntrega() { return tipoEntrega; }
        public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }
        public String getFechaEntrega() { return fechaEntrega; }
        public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }
        public String getVentanaEntrega() { return ventanaEntrega; }
        public void setVentanaEntrega(String ventanaEntrega) { this.ventanaEntrega = ventanaEntrega; }
        public String getTipoComprobante() { return tipoComprobante; }
        public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
        public java.util.List<ItemRequest> getItems() { return items; }
        public void setItems(java.util.List<ItemRequest> items) { this.items = items; }
    }

    public static class ItemRequest {
        private int productoId;
        private int cantidad;
        private Double montoSolicitadoEntero;

        public int getProductoId() { return productoId; }
        public void setProductoId(int productoId) { this.productoId = productoId; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public Double getMontoSolicitadoEntero() { return montoSolicitadoEntero; }
        public void setMontoSolicitadoEntero(Double montoSolicitadoEntero) { this.montoSolicitadoEntero = montoSolicitadoEntero; }
    }

    public static class RecalcularTotalesRequest {
        private int idDireccionEntrega;
        private java.util.List<ItemRequest> items;

        public int getIdDireccionEntrega() { return idDireccionEntrega; }
        public void setIdDireccionEntrega(int idDireccionEntrega) { this.idDireccionEntrega = idDireccionEntrega; }
        public java.util.List<ItemRequest> getItems() { return items; }
        public void setItems(java.util.List<ItemRequest> items) { this.items = items; }
    }

    public static class CalcularTotalesResponse {
        private final double subtotal;
        private final double costoEnvio;
        private final double total;

        public CalcularTotalesResponse(double subtotal, double costoEnvio, double total) {
            this.subtotal = subtotal;
            this.costoEnvio = costoEnvio;
            this.total = total;
        }

        public double getSubtotal() { return subtotal; }
        public double getCostoEnvio() { return costoEnvio; }
        public double getTotal() { return total; }
    }
}