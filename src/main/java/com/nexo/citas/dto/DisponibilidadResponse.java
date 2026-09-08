package com.nexo.citas.dto;

/**
 * Respuesta del endpoint de disponibilidad de una agenda medica
 * (cruce entre los slots de nexo-personal-api y las citas activas ya reservadas).
 */
public class DisponibilidadResponse {

    private String startTime;
    private String endTime;
    private Boolean available;

    public DisponibilidadResponse() {
    }

    public DisponibilidadResponse(String startTime, String endTime, Boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
