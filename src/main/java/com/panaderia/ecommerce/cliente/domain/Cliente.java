package com.panaderia.ecommerce.cliente.domain;

import java.util.List;
import java.util.Objects;
import com.panaderia.ecommerce.autenticacion.domain.Rol;

public class Cliente {
    private final Long id;
    private final String nombres;
    private final String apellidos;
    private final String email;
    private final String telefono;
    private final Ruc ruc;
    private final RazonSocial razonSocial;
    private final List<Direccion> direcciones;
    private final Rol rol;
    public Cliente(Long id, String nombres, String apellidos, String email, String telefono, Ruc ruc, RazonSocial razonSocial, List<Direccion> direcciones, Rol rol) {
        this.id = id;
        this.nombres = Objects.requireNonNull(nombres, "Nombres son obligatorios");
        this.apellidos = Objects.requireNonNull(apellidos, "Apellidos son obligatorios");
        this.email = Objects.requireNonNull(email, "Email es obligatorio");
        this.telefono = telefono;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direcciones = direcciones != null ? List.copyOf(direcciones) : List.of();
        this.rol = rol == null ? Rol.CLIENTE : rol;
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Ruc getRuc() {
        return ruc;
    }

    public RazonSocial getRazonSocial() {
        return razonSocial;
    }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public Rol getRol() {
        return rol;
    }

    public Cliente agregarDireccion(Direccion direccion) {
        List<Direccion> nuevasDirecciones = new java.util.ArrayList<>(direcciones);
        nuevasDirecciones.add(direccion);
        return new Cliente(id, nombres, apellidos, email, telefono, ruc, razonSocial, nuevasDirecciones, rol);
    }

    public Cliente eliminarDireccion(String alias) {
        List<Direccion> nuevasDirecciones = direcciones.stream()
                .filter(d -> !alias.equals(d.getAlias()))
                .toList();
        return new Cliente(id, nombres, apellidos, email, telefono, ruc, razonSocial, nuevasDirecciones, rol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}