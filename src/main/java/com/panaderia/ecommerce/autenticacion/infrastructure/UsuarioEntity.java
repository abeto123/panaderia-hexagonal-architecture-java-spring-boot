package com.panaderia.ecommerce.autenticacion.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "contrasenia", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.panaderia.ecommerce.autenticacion.domain.Rol rol;

    @Transient
    private Long clienteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public com.panaderia.ecommerce.autenticacion.domain.Rol getRol() {
        return rol;
    }

    public void setRol(com.panaderia.ecommerce.autenticacion.domain.Rol rol) {
        this.rol = rol;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}