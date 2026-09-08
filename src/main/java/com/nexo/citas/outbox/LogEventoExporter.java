package com.nexo.citas.outbox;

import com.nexo.citas.event.CitaEvento;
import com.nexo.citas.event.EventoExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Exportador por defecto (sin broker configurado).
 * Registra el evento en el log. Para produccion con Kafka/Rabbit se sustituye
 * esta implementacion (o se anade un exporter real dirigido al broker).
 */
@Component
public class LogEventoExporter implements EventoExporter {

    private static final Logger log = LoggerFactory.getLogger(LogEventoExporter.class);

    @Override
    public void exportar(CitaEvento evento) {
        log.info("[OUTBOX] publicando {} para cita {} con datos {}",
                evento.tipo(), evento.aggregateId(), evento.data());
    }
}
