package com.panaderia_barrios.pedido.carrito.infraestructure.output.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_producto")
public class CarritoProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_carrito", nullable = false)
    private Long idCarrito;

    @Column(name = "id_producto", nullable = false)
    private int idProducto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "monto_solicitado_entero")
    private Double montoSolicitadoEntero;

    public CarritoProductoEntity() {}

    public CarritoProductoEntity(Long idCarrito, int idProducto, int cantidad, Double montoSolicitadoEntero) {
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.montoSolicitadoEntero = montoSolicitadoEntero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getMontoSolicitadoEntero() {
        return montoSolicitadoEntero;
    }

    public void setMontoSolicitadoEntero(Double montoSolicitadoEntero) {
        this.montoSolicitadoEntero = montoSolicitadoEntero;
    }
}