package com.panaderia_barrios.pedido.pedido.domain;

import java.util.Collections;
import java.util.List;

import com.panaderia_barrios.pedido.cliente.domain.IdCliente;
import com.panaderia_barrios.pedido.shared.common.Dinero;
import com.panaderia_barrios.pedido.shared.common.FechaEntrega;
import com.panaderia_barrios.pedido.shared.common.TipoEntrega;

public class Pedido {
    private final Long id;
    private final IdCliente idCliente;
    private final Integer idSede;
    private final Integer idDireccionEntrega;
    private final TipoEntrega tipoEntrega;
    private final FechaEntrega fechaEntrega;
    private final String ventanaEntrega;
    private final Dinero subtotal;
    private final Dinero costoEnvio;
    private final Dinero total;
    private final String estado;
    private final String tipoComprobante;
    private final List<PedidoItem> items;

    public Pedido(Long id, IdCliente idCliente, Integer idSede, Integer idDireccionEntrega, TipoEntrega tipoEntrega,
                  FechaEntrega fechaEntrega, String ventanaEntrega, Dinero subtotal, Dinero costoEnvio,
                  String estado, String tipoComprobante, List<PedidoItem> items) {
        this.id = id;
        this.idCliente = idCliente;
        this.idSede = idSede;
        this.idDireccionEntrega = idDireccionEntrega;
        this.tipoEntrega = tipoEntrega;
        this.fechaEntrega = fechaEntrega;
        this.ventanaEntrega = ventanaEntrega;
        this.subtotal = subtotal;
        this.costoEnvio = costoEnvio != null ? costoEnvio : new Dinero(0);
        this.total = this.subtotal.sumar(this.costoEnvio);
        this.estado = estado;
        this.tipoComprobante = tipoComprobante;
        this.items = items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
    }

    public Long getId() {
        return id;
    }

    public IdCliente getIdCliente() {
        return idCliente;
    }

    public Integer getIdSede() {
        return idSede;
    }

    public Integer getIdDireccionEntrega() {
        return idDireccionEntrega;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public FechaEntrega getFechaEntrega() {
        return fechaEntrega;
    }

    public String getVentanaEntrega() {
        return ventanaEntrega;
    }

    public double getTotal() {
        return total.getMonto();
    }

    public double getSubtotal() {
        return subtotal.getMonto();
    }

    public Dinero getCostoEnvio() {
        return costoEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public List<PedidoItem> getItems() {
        return items;
    }
}