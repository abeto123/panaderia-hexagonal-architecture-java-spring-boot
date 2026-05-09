package com.panaderia_barrios.pedido.carrito.infraestructure.output.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito")
public class CarritoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    public CarritoEntity() {}

    public CarritoEntity(int idCliente) {
        this.idCliente = idCliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}