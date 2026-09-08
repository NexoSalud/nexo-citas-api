package com.nexo.citas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JPA Entity del slot de disponibilidad de consultorio.
 * El UNIQUE (consultorio_id, fecha, hora_inicio) + bloqueo optimista (version)
 * evita el doble-book.
 */
@Entity
@Table(name = "disponibilidad_slot")
public class DisponibilidadSlotEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "consultorio_id", nullable = false)
    private Long consultorioId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "reservada_por", length = 36)
    private String reservadaPor;

    @Version
    @Column(name = "version")
    private Long version;

    public DisponibilidadSlotEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(Long consultorioId) {
        this.consultorioId = consultorioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getReservadaPor() {
        return reservadaPor;
    }

    public void setReservadaPor(String reservadaPor) {
        this.reservadaPor = reservadaPor;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
