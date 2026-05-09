package com.panaderia_barrios.pedido.carrito.infraestructure.input.web;

import com.panaderia_barrios.pedido.carrito.domain.Carrito;
import com.panaderia_barrios.pedido.carrito.domain.CarritoItem;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.AgregarProductoCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.EliminarProductoCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.ObtenerCarritoUseCase;
import com.panaderia_barrios.pedido.carrito.domain.ports.input.VaciarCarritoUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    private final AgregarProductoCarritoUseCase agregarUseCase;
    private final EliminarProductoCarritoUseCase eliminarUseCase;
    private final ObtenerCarritoUseCase obtenerUseCase;
    private final VaciarCarritoUseCase vaciarUseCase;

    public CarritoController(AgregarProductoCarritoUseCase agregarUseCase,
                             EliminarProductoCarritoUseCase eliminarUseCase,
                             ObtenerCarritoUseCase obtenerUseCase,
                             VaciarCarritoUseCase vaciarUseCase) {
        this.agregarUseCase = agregarUseCase;
        this.eliminarUseCase = eliminarUseCase;
        this.obtenerUseCase = obtenerUseCase;
        this.vaciarUseCase = vaciarUseCase;
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable int clienteId) {
        return ResponseEntity.ok(obtenerUseCase.obtener(clienteId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarProducto(@RequestBody AgregarProductoRequest request) {
        CarritoItem item = new CarritoItem(
                request.getIdProducto(),
                request.getCantidad(),
                request.getMontoSolicitadoEntero(),
                null,
                null,
                0.0
        );
        Carrito carrito = agregarUseCase.agregar(request.getIdCliente(), item);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/eliminar")
    public ResponseEntity<Boolean> eliminarProducto(@RequestBody EliminarProductoRequest request) {
        return ResponseEntity.ok(eliminarUseCase.eliminar(request.getIdCliente(), request.getIdProducto()));
    }

    @PostMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@RequestBody VaciarCarritoRequest request) {
        vaciarUseCase.vaciar(request.getIdCliente());
        return ResponseEntity.ok().build();
    }

    public static class AgregarProductoRequest {
        private int idCliente;
        private int idProducto;
        private int cantidad;
        private Double montoSolicitadoEntero;

        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
        public int getIdProducto() { return idProducto; }
        public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public Double getMontoSolicitadoEntero() { return montoSolicitadoEntero; }
        public void setMontoSolicitadoEntero(Double montoSolicitadoEntero) { this.montoSolicitadoEntero = montoSolicitadoEntero; }
    }

    public static class EliminarProductoRequest {
        private int idCliente;
        private int idProducto;

        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
        public int getIdProducto() { return idProducto; }
        public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    }

    public static class VaciarCarritoRequest {
        private int idCliente;

        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    }
}