package com.panaderia_barrios.pedido.catalogo.domain;

public class Producto {
    private final int id;
    private final String nombre;
    private final String descripcion;
    private final double precioB2c;
    private final int unidadesBaseB2b;
    private final double solesBaseB2b;
    private final int unidadMinimaB2b;
    private final boolean disponibleB2b;
    private final int idCategoria;
    private final String foto;

    public Producto(int id, String nombre, String descripcion, double precioB2c,
                   int unidadesBaseB2b, double solesBaseB2b, int unidadMinimaB2b,
                   boolean disponibleB2b, int idCategoria, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioB2c = precioB2c;
        this.unidadesBaseB2b = unidadesBaseB2b;
        this.solesBaseB2b = solesBaseB2b;
        this.unidadMinimaB2b = unidadMinimaB2b;
        this.disponibleB2b = disponibleB2b;
        this.idCategoria = idCategoria;
        this.foto = foto;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioB2c() { return precioB2c; }
    public int getUnidadesBaseB2b() { return unidadesBaseB2b; }
    public double getSolesBaseB2b() { return solesBaseB2b; }
    public int getUnidadMinimaB2b() { return unidadMinimaB2b; }
    public boolean isDisponibleB2b() { return disponibleB2b; }
    public int getIdCategoria() { return idCategoria; }
    public String getFoto() { return foto; }

    public boolean esPack() {
        // Assuming packs have specific category or logic
        return idCategoria == 1; // Example, adjust based on data
    }
}