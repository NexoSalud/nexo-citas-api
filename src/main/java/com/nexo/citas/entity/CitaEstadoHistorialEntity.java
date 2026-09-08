package com.nexo.citas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA Entity del historial / trazabilidad de la maquina de estados de la cita.
 */
@Entity
@Table(name = "cita_estado_historial")
public class CitaEstadoHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cita_id", length = 36, nullable = false)
    private String citaId;

    @Column(name = "estado", length = 24, nullable = false)
    private String estado;

    @Column(name = "actor", length = 36)
    private String actor;

    @Column(name = "canal", length = 12)
    private String canal;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public CitaEstadoHistorialEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCitaId() {
        return citaId;
    }

    public void setCitaId(String citaId) {
        this.citaId = citaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
