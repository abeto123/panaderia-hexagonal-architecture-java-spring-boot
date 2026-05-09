package com.panaderia_barrios.pedido.cliente.domain;

public class Cliente {
    private final int id;
    private final String nombre;
    private final String apellidos;
    private final String email;
    private final String telefono;
    private final String contrasenia;
    private final String rol;
    private final String ruc; // Optional for B2B
    private final String razonSocial; // Optional for B2B

    public Cliente(int id, String nombre, String apellidos, String email, String telefono, String contrasenia, String rol) {
        this(id, nombre, apellidos, email, telefono, contrasenia, rol, null, null);
    }

    public Cliente(int id, String nombre, String apellidos, String email, String telefono, String contrasenia, String rol, String ruc, String razonSocial) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getContrasenia() { return contrasenia; }
    public String getRol() { return rol; }
    public String getRuc() { return ruc; }
    public String getRazonSocial() { return razonSocial; }

    public boolean isB2B() {
        return "EMPRESA_FACTURA".equals(rol);
    }
}