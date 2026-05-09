package com.panaderia_barrios.pedido.catalogo.infraestructure.output.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "producto")
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(name = "precio_b2c", nullable = false)
    private double precioB2c;

    @Column(name = "unidades_base_b2b")
    private int unidadesBaseB2b;

    @Column(name = "soles_base_b2b")
    private double solesBaseB2b;

    @Column(name = "unidad_minima_b2b")
    private int unidadMinimaB2b;

    @Column(name = "disponible_b2b")
    private boolean disponibleB2b;

    @Column(name = "id_categoria")
    private int idCategoria;

    @Column
    private String foto;

    // Constructors
    public ProductoEntity() {}

    public ProductoEntity(String nombre, String descripcion, double precioB2c,
                         int unidadesBaseB2b, double solesBaseB2b, int unidadMinimaB2b,
                         boolean disponibleB2b, int idCategoria, String foto) {
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

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioB2c() { return precioB2c; }
    public void setPrecioB2c(double precioB2c) { this.precioB2c = precioB2c; }

    public int getUnidadesBaseB2b() { return unidadesBaseB2b; }
    public void setUnidadesBaseB2b(int unidadesBaseB2b) { this.unidadesBaseB2b = unidadesBaseB2b; }

    public double getSolesBaseB2b() { return solesBaseB2b; }
    public void setSolesBaseB2b(double solesBaseB2b) { this.solesBaseB2b = solesBaseB2b; }

    public int getUnidadMinimaB2b() { return unidadMinimaB2b; }
    public void setUnidadMinimaB2b(int unidadMinimaB2b) { this.unidadMinimaB2b = unidadMinimaB2b; }

    public boolean isDisponibleB2b() { return disponibleB2b; }
    public void setDisponibleB2b(boolean disponibleB2b) { this.disponibleB2b = disponibleB2b; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}