package com.panaderia.ecommerce.autenticacion.domain;

import java.util.Objects;

public class Usuario {
    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Rol rol;
    private final Long clienteId;

    public Usuario(Long id, String email, String passwordHash, Rol rol, Long clienteId) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email es obligatorio");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password es obligatorio");
        this.rol = rol != null ? rol : Rol.CLIENTE;
        this.clienteId = clienteId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public Long getClienteId() {
        return clienteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
}