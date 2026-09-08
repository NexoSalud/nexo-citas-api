package com.nexo.citas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity del catalogo dinamico de tipos de cita / especialidades (data-driven).
 * Permite registrar nuevas especialidades sin modificar el codigo.
 */
@Entity
@Table(name = "especialidad_tipo")
public class EspecialidadTipoEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "codigo", length = 64, nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 160, nullable = false)
    private String nombre;

    @Column(name = "requiere_remision", nullable = false)
    private Boolean requiereRemision;

    @Column(name = "habilitada", nullable = false)
    private Boolean habilitada;

    public EspecialidadTipoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getRequiereRemision() {
        return requiereRemision;
    }

    public void setRequiereRemision(Boolean requiereRemision) {
        this.requiereRemision = requiereRemision;
    }

    public Boolean getHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
    }
}
