package com.nexo.citas.event;

/**
 * Tipos de eventos de integracion del contexto Citas.
 * Publicados en el outbox transaccional y consumidos por Facturacion y Admin Core.
 */
public enum TipoEvento {

    CITACA_CREADA("appointment.created"),
    CITA_FACTURADA("appointment.factured"),
    CITA_CANCELADA("appointment.cancelled"),
    CITA_COMPLETADA("appointment.completed"),
    CITA_APLICADA("appointment.applied"),
    CITA_NO_ASISTIO("appointment.no_show"),
    CITA_REAGENDADA("appointment.rescheduled"),
    PIPELINE_SUGERIDO("pipeline.suggested"),
    PACIENTE_PERFIL_ACTUALIZADO("patient.profile.updated");

    private final String topic;

    TipoEvento(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public static TipoEvento valueOfByTopic(String topic) {
        for (TipoEvento t : values()) {
            if (t.topic.equals(topic)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Topic de evento desconocido: " + topic);
    }
}
