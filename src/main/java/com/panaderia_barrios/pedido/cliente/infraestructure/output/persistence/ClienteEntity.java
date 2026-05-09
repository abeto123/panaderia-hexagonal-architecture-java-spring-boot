package com.panaderia_barrios.pedido.cliente.infraestructure.output.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String contrasenia;

    @Column(nullable = false)
    private String rol;

    @Column
    private String ruc;

    @Column(name = "razon_social")
    private String razonSocial;

    // Constructors
    public ClienteEntity() {}

    public ClienteEntity(String nombre, String apellidos, String email, String telefono, String contrasenia, String rol, String ruc, String razonSocial) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}