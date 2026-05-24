package com.panaderia.ecommerce.pedido.domain;

import com.panaderia.ecommerce.cliente.domain.Cliente;
import com.panaderia.ecommerce.cliente.domain.Direccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private final Long id;
    private final Long clienteId;
    private final List<ItemPedido> items;
    private final TipoEntrega tipoEntrega;
    private final Direccion direccionEntrega;
    private final CostoEnvio costoEnvio;
    private final FechaEntrega fechaEntrega;
    private final EstadoPedido estado;
    private final LocalDateTime fechaRegistro;
    private final BigDecimal subtotal;
    private final BigDecimal total;

    public Pedido(Long id, Long clienteId, List<ItemPedido> items, TipoEntrega tipoEntrega, Direccion direccionEntrega,
                  CostoEnvio costoEnvio, FechaEntrega fechaEntrega, EstadoPedido estado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "Cliente es obligatorio");
        this.items = List.copyOf(items);
        this.tipoEntrega = tipoEntrega;
        this.direccionEntrega = direccionEntrega;
        this.costoEnvio = costoEnvio;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado != null ? estado : EstadoPedido.PENDIENTE;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
        this.subtotal = calcularSubtotal();
        this.total = subtotal.add(costoEnvio != null ? costoEnvio.getValor() : BigDecimal.ZERO);
    }

    private BigDecimal calcularSubtotal() {
        return items.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public Direccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public CostoEnvio getCostoEnvio() {
        return costoEnvio;
    }

    public FechaEntrega getFechaEntrega() {
        return fechaEntrega;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Pedido cambiarEstado(EstadoPedido nuevoEstado) {
        return new Pedido(id, clienteId, items, tipoEntrega, direccionEntrega, costoEnvio, fechaEntrega, nuevoEstado, fechaRegistro);
    }

    public boolean puedeCambiarEstado(EstadoPedido nuevoEstado) {
        // Lógica de transición de estados
        return true; // Simplificado
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }
}