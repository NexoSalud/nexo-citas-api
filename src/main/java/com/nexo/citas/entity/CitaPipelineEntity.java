package com.nexo.citas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity del segmento del pipeline 3280.
 * Relaciona una cita origen con su cita preventiva hija, en orden secuencial.
 */
@Entity
@Table(name = "cita_pipeline")
public class CitaPipelineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cita_origen_id", length = 36, nullable = false)
    private String citaOrigenId;

    @Column(name = "cita_hija_id", length = 36)
    private String citaHijaId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "estado", length = 24, nullable = false)
    private String estado;

    public CitaPipelineEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCitaOrigenId() {
        return citaOrigenId;
    }

    public void setCitaOrigenId(String citaOrigenId) {
        this.citaOrigenId = citaOrigenId;
    }

    public String getCitaHijaId() {
        return citaHijaId;
    }

    public void setCitaHijaId(String citaHijaId) {
        this.citaHijaId = citaHijaId;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
