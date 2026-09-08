package com.nexo.citas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity de la regla 3280 data-driven. La condicion se guarda como JSON
 * (texto) y la evalua CondicionEvaluador en el dominio.
 */
@Entity
@Table(name = "regla_3280")
public class Regla3280Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "especialidad_origen_id")
    private Long especialidadOrigenId;

    @Column(name = "condicion", columnDefinition = "text", nullable = false)
    private String condicion;

    @Column(name = "tipo_cita_sugerida_id")
    private Long tipoCitaSugeridaId;

    @Column(name = "tipo_cita_sugerida_nombre", length = 160)
    private String tipoCitaSugeridaNombre;

    @Column(name = "dias_ventana")
    private Integer diasVentana;

    @Column(name = "prioridad")
    private Integer prioridad;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "habilitada", nullable = false)
    private Boolean habilitada;

    public Regla3280Entity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEspecialidadOrigenId() {
        return especialidadOrigenId;
    }

    public void setEspecialidadOrigenId(Long especialidadOrigenId) {
        this.especialidadOrigenId = especialidadOrigenId;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Long getTipoCitaSugeridaId() {
        return tipoCitaSugeridaId;
    }

    public void setTipoCitaSugeridaId(Long tipoCitaSugeridaId) {
        this.tipoCitaSugeridaId = tipoCitaSugeridaId;
    }

    public String getTipoCitaSugeridaNombre() {
        return tipoCitaSugeridaNombre;
    }

    public void setTipoCitaSugeridaNombre(String tipoCitaSugeridaNombre) {
        this.tipoCitaSugeridaNombre = tipoCitaSugeridaNombre;
    }

    public Integer getDiasVentana() {
        return diasVentana;
    }

    public void setDiasVentana(Integer diasVentana) {
        this.diasVentana = diasVentana;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
    }
}
