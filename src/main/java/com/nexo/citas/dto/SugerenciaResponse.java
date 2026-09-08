package com.nexo.citas.dto;

import com.nexo.citas.domain.reglas.Sugerencia;

/**
 * Respuesta de sugerencia preventiva del motor 3280 (pipeline).
 */
public class SugerenciaResponse {

    private Long tipoCitaId;
    private String tipoCitaNombre;
    private Integer diasVentana;
    private int prioridad;

    public SugerenciaResponse() {
    }

    public static SugerenciaResponse fromDomain(Sugerencia s) {
        SugerenciaResponse r = new SugerenciaResponse();
        r.setTipoCitaId(s.getTipoCitaSugeridaId());
        r.setTipoCitaNombre(s.getTipoCitaNombre());
        r.setDiasVentana(s.getDiasVentana());
        r.setPrioridad(s.getPrioridad());
        return r;
    }

    public Long getTipoCitaId() {
        return tipoCitaId;
    }

    public void setTipoCitaId(Long tipoCitaId) {
        this.tipoCitaId = tipoCitaId;
    }

    public String getTipoCitaNombre() {
        return tipoCitaNombre;
    }

    public void setTipoCitaNombre(String tipoCitaNombre) {
        this.tipoCitaNombre = tipoCitaNombre;
    }

    public Integer getDiasVentana() {
        return diasVentana;
    }

    public void setDiasVentana(Integer diasVentana) {
        this.diasVentana = diasVentana;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}
