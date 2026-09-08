package com.nexo.citas.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.entity.OutboxEventEntity;
import com.nexo.citas.event.CitaEvento;
import com.nexo.citas.event.TipoEvento;
import com.nexo.citas.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de aplicacion para publicar eventos de integracion de forma
 * transaccional (patron Outbox). El evento se persiste en la MISMA transaccion
 * que el cambio de estado de la cita, garantizando consistencia.
 */
@Service
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Persiste un evento en el outbox dentro de la transaccion llamadora.
     */
    @Transactional
    public void publicar(String aggregateId, TipoEvento tipo, Map<String, Object> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("aggregateId", aggregateId);
        payload.put("eventType", tipo.name());
        payload.put("data", data != null ? data : Map.of());

        OutboxEventEntity evt = new OutboxEventEntity();
        evt.setAggregateId(aggregateId);
        evt.setTipo(tipo.getTopic());
        evt.setPayload(writeJson(payload));
        evt.setStatus("PENDING");
        evt.setCreatedAt(LocalDateTime.now());
        outboxRepository.save(evt);
        log.debug("Evento {} encolado en outbox para cita {}", tipo.getTopic(), aggregateId);
    }

    private String writeJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo serializar payload del evento", e);
        }
    }
}
