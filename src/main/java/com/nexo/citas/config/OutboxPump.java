package com.nexo.citas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.entity.OutboxEventEntity;
import com.nexo.citas.event.CitaEvento;
import com.nexo.citas.event.EventoExporter;
import com.nexo.citas.event.TipoEvento;
import com.nexo.citas.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Bomba (pump) del outbox: envia los eventos PENDING al exporter cada N ms.
 * Si el exporter lanza excepcion, el evento queda PENDING para reintento.
 */
@Component
@EnableScheduling
public class OutboxPump {

    private static final Logger log = LoggerFactory.getLogger(OutboxPump.class);

    private final OutboxRepository outboxRepository;
    private final EventoExporter eventoExporter;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public OutboxPump(OutboxRepository outboxRepository,
                      EventoExporter eventoExporter,
                      ObjectMapper objectMapper,
                      @Value("${nexo.outbox.enabled:true}") boolean enabled) {
        this.outboxRepository = outboxRepository;
        this.eventoExporter = eventoExporter;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${nexo.outbox.poll-interval-ms:2000}")
    @Transactional
    public void drenar() {
        if (!enabled) {
            return;
        }
        List<OutboxEventEntity> pendientes =
                outboxRepository.findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc("PENDING", LocalDateTime.now());
        for (OutboxEventEntity evt : pendientes) {
            try {
                CitaEvento evento = toEvento(evt);
                eventoExporter.exportar(evento);
                evt.setStatus("SENT");
                evt.setSentAt(LocalDateTime.now());
                outboxRepository.save(evt);
            } catch (Exception e) {
                evt.setStatus("FAILED");
                outboxRepository.save(evt);
                log.error("Fallo el envio del evento {} (cita {}): {}", evt.getTipo(), evt.getAggregateId(), e.getMessage());
            }
        }
    }

    private CitaEvento toEvento(OutboxEventEntity evt) throws Exception {
        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(evt.getPayload());
        var dataNode = root.get("data");
        Map<String, Object> data = unmarshal(dataNode);
        return new CitaEvento(
                null,
                evt.getAggregateId(),
                TipoEvento.valueOfByTopic(evt.getTipo()),
                null,
                data,
                evt.getCreatedAt());
    }

    private java.util.Map<String, Object> unmarshal(com.fasterxml.jackson.databind.JsonNode node) throws Exception {
        if (node == null || node.isNull()) {
            return java.util.Map.of();
        }
        return objectMapper.convertValue(node, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
    }
}
