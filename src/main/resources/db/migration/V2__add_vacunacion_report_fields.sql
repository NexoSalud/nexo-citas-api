-- ---------------------------------------------------------------
-- Nexo 2.0 · Microservicio de Citas (Agendamiento)
-- Migración V2: campos de vacunación/reporte PAI en la cita
-- (alimentan el módulo de reportes mensuales de vacunación)
-- ---------------------------------------------------------------

ALTER TABLE cita
    ADD COLUMN IF NOT EXISTS municipio          VARCHAR(120),
    ADD COLUMN IF NOT EXISTS codigo_dane        VARCHAR(8),
    ADD COLUMN IF NOT EXISTS tipo_identificacion VARCHAR(60),
    ADD COLUMN IF NOT EXISTS regimen            VARCHAR(60),
    ADD COLUMN IF NOT EXISTS aseguradora        VARCHAR(120),
    ADD COLUMN IF NOT EXISTS dosis              VARCHAR(60),
    ADD COLUMN IF NOT EXISTS vacuna             VARCHAR(120);

CREATE INDEX IF NOT EXISTS idx_cita_vacunacion ON cita(especialidad_tipo_id, estado);
