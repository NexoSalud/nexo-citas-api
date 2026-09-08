package com.nexo.citas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO de un slot de disponibilidad crudo devuelto por
 * GET /api/v1/medical-agenda/{id}/available-slots de nexo-personal-api.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlotDto {

    private String startTime;
    private String endTime;

    public SlotDto() {
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
}
