package com.nexo.citas.event;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Valor de un evento de dominio de Citas (record inmutable).
 * Agrega la auditoria del evento y su contexto.
 */
public record CitaEvento(
        String eventId,
        String aggregateId,
        TipoEvento tipo,
        String tipoCitaSugerida,
        Map<String, Object> data,
        LocalDateTime ocurredAt
) {

    public static CitaEvento of(String aggregateId, TipoEvento tipo, Map<String, Object> data) {
        return new CitaEvento(
                java.util.UUID.randomUUID().toString(),
                aggregateId,
                tipo,
                null,
                data,
                LocalDateTime.now());
    }
}
