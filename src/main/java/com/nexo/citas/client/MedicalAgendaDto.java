package com.nexo.citas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * DTO minimo de lectura de la Agenda Medica expuesta por nexo-personal-api
 * (GET /api/v1/medical-agenda/{id}). Solo se mapean los campos que
 * nexo-citas-api necesita para validar disponibilidad; el resto de la
 * respuesta se ignora.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicalAgendaDto {

    private String startDate;
    private String endDate;
    private List<String> workDays;
    private String startTime;
    private String endTime;
    private String agendaState;
    private Boolean allowGroupSession;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(List<String> workDays) {
        this.workDays = workDays;
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

    public String getAgendaState() {
        return agendaState;
    }

    public void setAgendaState(String agendaState) {
        this.agendaState = agendaState;
    }

    public Boolean getAllowGroupSession() {
        return allowGroupSession;
    }

    public void setAllowGroupSession(Boolean allowGroupSession) {
        this.allowGroupSession = allowGroupSession;
    }
}
