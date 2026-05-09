package com.panaderia_barrios.pedido.pedido.infraestructure.output.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.panaderia_barrios.pedido.envio.infraestructure.output.persistence.TipoEntregaEntity;

@Entity
@Table(name = "pedido")
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @Column(name = "id_sede")
    private Integer idSede;

    @Column(name = "id_direccion_entrega")
    private Integer idDireccionEntrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false)
    private TipoEntregaEntity tipoEntrega;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Column(name = "ventana_entrega")
    private String ventanaEntrega;

    @Column(name = "subtotal_productos", nullable = false)
    private double subtotalProductos;

    @Column(name = "costo_envio", nullable = false)
    private double costoEnvio;

    @Column(name = "costo_total", nullable = false)
    private double costoTotal;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "tipo_comprobante")
    private String tipoComprobante;

    // Constructors, getters, setters

    public PedidoEntity() {}

    public PedidoEntity(int idCliente, Integer idSede, Integer idDireccionEntrega,
                       TipoEntregaEntity tipoEntrega, LocalDate fechaEntrega,
                       String ventanaEntrega, double subtotalProductos,
                       double costoEnvio, double costoTotal, String estado,
                       String tipoComprobante) {
        this.idCliente = idCliente;
        this.idSede = idSede;
        this.idDireccionEntrega = idDireccionEntrega;
        this.tipoEntrega = tipoEntrega;
        this.fechaEntrega = fechaEntrega;
        this.ventanaEntrega = ventanaEntrega;
        this.subtotalProductos = subtotalProductos;
        this.costoEnvio = costoEnvio;
        this.costoTotal = costoTotal;
        this.estado = estado;
        this.tipoComprobante = tipoComprobante;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer idSede) { this.idSede = idSede; }

    public Integer getIdDireccionEntrega() { return idDireccionEntrega; }
    public void setIdDireccionEntrega(Integer idDireccionEntrega) { this.idDireccionEntrega = idDireccionEntrega; }

    public TipoEntregaEntity getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(TipoEntregaEntity tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getVentanaEntrega() { return ventanaEntrega; }
    public void setVentanaEntrega(String ventanaEntrega) { this.ventanaEntrega = ventanaEntrega; }

    public double getSubtotalProductos() { return subtotalProductos; }
    public void setSubtotalProductos(double subtotalProductos) { this.subtotalProductos = subtotalProductos; }

    public double getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(double costoEnvio) { this.costoEnvio = costoEnvio; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
}