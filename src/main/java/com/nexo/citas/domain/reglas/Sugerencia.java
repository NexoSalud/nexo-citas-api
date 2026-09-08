package com.nexo.citas.domain.reglas;

/**
 * Resultado del motor 3280: una cita preventiva sugerida para el paciente.
 */
public class Sugerencia {

    private final Long tipoCitaSugeridaId;
    private final String tipoCitaNombre;
    private final Integer diasVentana;
    private final int prioridad;

    public Sugerencia(Long tipoCitaSugeridaId, String tipoCitaNombre, Integer diasVentana, int prioridad) {
        this.tipoCitaSugeridaId = tipoCitaSugeridaId;
        this.tipoCitaNombre = tipoCitaNombre;
        this.diasVentana = diasVentana;
        this.prioridad = prioridad;
    }

    public Long getTipoCitaSugeridaId() {
        return tipoCitaSugeridaId;
    }

    public String getTipoCitaNombre() {
        return tipoCitaNombre;
    }

    public Integer getDiasVentana() {
        return diasVentana;
    }

    public int getPrioridad() {
        return prioridad;
    }
}
