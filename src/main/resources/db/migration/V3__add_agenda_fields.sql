-- ---------------------------------------------------------------
-- Nexo 2.0 · Microservicio de Citas (Agendamiento)
-- Migración V3: campos de agenda/administrativos (integración con
-- nexo-personal-api / Agenda Médica) en la cita.
-- ---------------------------------------------------------------

ALTER TABLE cita
    ADD COLUMN IF NOT EXISTS agenda_id                       BIGINT,
    ADD COLUMN IF NOT EXISTS sede_id                          BIGINT,
    ADD COLUMN IF NOT EXISTS modalidad                        VARCHAR(20),
    ADD COLUMN IF NOT EXISTS funcionalidad                    VARCHAR(60),
    ADD COLUMN IF NOT EXISTS rotulo                           VARCHAR(120),
    ADD COLUMN IF NOT EXISTS observaciones_administrativas    TEXT,
    ADD COLUMN IF NOT EXISTS turno_doble                      BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS agenda_grupal                    BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS notificacion_automatica          BOOLEAN DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_cita_agenda_fecha ON cita(agenda_id, fecha, hora_inicio);
