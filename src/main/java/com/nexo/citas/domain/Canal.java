package com.nexo.citas.domain;

import java.util.Arrays;

/**
 * Value Object: Canal de agendamiento (omnicanalidad).
 * Soporta Portal Web, IVR/telefonica y presencial mediante personal de servicio.
 * Todas ellas comparten el mismo caso de uso y la misma maquina de estados.
 */
public enum Canal {
    WEB,
    IVR,
    PRESENCIAL;

    public static Canal from(String value) {
        if (value == null || value.isBlank()) {
            return PRESENCIAL;
        }
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(PRESENCIAL);
    }
}
