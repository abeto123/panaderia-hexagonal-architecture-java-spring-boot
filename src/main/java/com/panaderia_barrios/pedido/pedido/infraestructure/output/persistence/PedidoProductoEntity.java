package com.panaderia_barrios.pedido.pedido.infraestructure.output.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido_producto")
public class PedidoProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idPedido;
    private int idProducto;
    private int cantidad;
    private Double montoSolicitadoEntero;
    private double precioUnitarioCongelado;
    private double subtotal;

    public PedidoProductoEntity() {
    }

    public PedidoProductoEntity(Long idPedido, int idProducto, int cantidad, Double montoSolicitadoEntero,
                                double precioUnitarioCongelado, double subtotal) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.montoSolicitadoEntero = montoSolicitadoEntero;
        this.precioUnitarioCongelado = precioUnitarioCongelado;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Double getMontoSolicitadoEntero() {
        return montoSolicitadoEntero;
    }

    public double getPrecioUnitarioCongelado() {
        return precioUnitarioCongelado;
    }

    public double getSubtotal() {
        return subtotal;
    }
}