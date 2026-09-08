package com.nexo.citas.event;

/**
 * Contrato que publica los eventos del outbox hacia el broker externo
 * (Kafka/Rabbit/SOAP de notificacion). En el entorno sin broker, la
 * implementacion por defecto solo marca el evento como SENT (log).
 */
public interface EventoExporter {

    /**
     * Exporta un evento hacia el destino externo. Debe lanzar excepcion si
     * falla para que el outbox lo reintente.
     */
    void exportar(CitaEvento evento);
}
