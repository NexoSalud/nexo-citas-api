package com.nexo.citas.domain.reglas;

/**
 * Regla 3280 data-driven: modelo evaluable del registro en tabla regla_3280.
 * Se mantiene separado de la capa JPA/persistencia (DDD).
 */
public class Regla3280 {

    private final Long id;
    private final Long especialidadOrigenId; // null = cualquier especialidad
    private final String condicionJson;      // JSONB evaluable por CondicionEvaluador
    private final Long tipoCitaSugeridaId;
    private final String tipoCitaSugeridaNombre;
    private final Integer diasVentana;
    private final int prioridad;
    private final boolean habilitada;

    public Regla3280(Long id, Long especialidadOrigenId, String condicionJson,
                     Long tipoCitaSugeridaId, String tipoCitaSugeridaNombre,
                     Integer diasVentana, int prioridad, boolean habilitada) {
        this.id = id;
        this.especialidadOrigenId = especialidadOrigenId;
        this.condicionJson = condicionJson;
        this.tipoCitaSugeridaId = tipoCitaSugeridaId;
        this.tipoCitaSugeridaNombre = tipoCitaSugeridaNombre;
        this.diasVentana = diasVentana;
        this.prioridad = prioridad;
        this.habilitada = habilitada;
    }

    public Long getId() {
        return id;
    }

    public Long getEspecialidadOrigenId() {
        return especialidadOrigenId;
    }

    public String getCondicionJson() {
        return condicionJson;
    }

    public Long getTipoCitaSugeridaId() {
        return tipoCitaSugeridaId;
    }

    public String getTipoCitaSugeridaNombre() {
        return tipoCitaSugeridaNombre;
    }

    public Integer getDiasVentana() {
        return diasVentana;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public boolean isHabilitada() {
        return habilitada;
    }
}
