-- ---------------------------------------------------------------
-- Nexo 2.0 · Microservicio de Citas (Agendamiento)
-- Migración V1: esquema del Core Domain + datos iniciales (reglas 3280)
-- ---------------------------------------------------------------

-- Catálogo dinámico de tipos de cita / especialidades (data-driven)
CREATE TABLE IF NOT EXISTS especialidad_tipo (
    id                 BIGINT PRIMARY KEY,
    codigo             VARCHAR(64) NOT NULL UNIQUE,
    nombre             VARCHAR(160) NOT NULL,
    requiere_remision  BOOLEAN NOT NULL DEFAULT FALSE,
    habilitada         BOOLEAN NOT NULL DEFAULT TRUE
);

-- Cita (agregado raíz) — hereda campos de auditoría en la capa JPA
CREATE TABLE IF NOT EXISTS cita (
    id                        VARCHAR(36) PRIMARY KEY,
    numero                    VARCHAR(32) NOT NULL UNIQUE,
    paciente_id               VARCHAR(36) NOT NULL,
    usuario_creador           VARCHAR(36),
    especialidad_tipo_id      BIGINT REFERENCES especialidad_tipo(id),
    consultorio_id            BIGINT,
    ubicacion                 VARCHAR(500),
    fecha                     TIMESTAMP NOT NULL,
    hora_inicio               TIMESTAMP NOT NULL,
    hora_fin                  TIMESTAMP NOT NULL,
    canal                     VARCHAR(12) NOT NULL,
    perfil_paciente_snapshot  TEXT,
    estado                    VARCHAR(24) NOT NULL,
    causa_cancelacion         VARCHAR(64),
    version                   BIGINT DEFAULT 0,
    created_at                TIMESTAMP NOT NULL DEFAULT now(),
    updated_at                TIMESTAMP,
    created_by                VARCHAR(36),
    updated_by                VARCHAR(36)
);
CREATE INDEX IF NOT EXISTS idx_cita_paciente ON cita(paciente_id);
CREATE INDEX IF NOT EXISTS idx_cita_estado   ON cita(estado);

-- Pipeline 3280 (segmentos secuenciales)
CREATE TABLE IF NOT EXISTS cita_pipeline (
    id               BIGSERIAL PRIMARY KEY,
    cita_origen_id   VARCHAR(36) NOT NULL REFERENCES cita(id),
    cita_hija_id     VARCHAR(36) REFERENCES cita(id),
    orden            INT NOT NULL,
    estado           VARCHAR(24) NOT NULL,
    UNIQUE (cita_origen_id, orden)
);
CREATE INDEX IF NOT EXISTS idx_pipeline_origen ON cita_pipeline(cita_origen_id);

-- Reglas 3280 data-driven (condición JSONB como texto, se evalúa en dominio)
CREATE TABLE IF NOT EXISTS regla_3280 (
    id                       BIGSERIAL PRIMARY KEY,
    especialidad_origen_id   BIGINT REFERENCES especialidad_tipo(id),
    condicion                TEXT NOT NULL,
    tipo_cita_sugerida_id    BIGINT,
    tipo_cita_sugerida_nombre VARCHAR(160),
    dias_ventana             INT,
    prioridad                INT DEFAULT 0,
    version                  INT DEFAULT 1,
    habilitada               BOOLEAN DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS idx_regla3280_esp ON regla_3280(especialidad_origen_id, habilitada);

-- Disponibilidad de consultorio (anti doble-book: UNIQUE + bloqueo optimista)
CREATE TABLE IF NOT EXISTS disponibilidad_slot (
    id              BIGSERIAL PRIMARY KEY,
    consultorio_id  BIGINT NOT NULL,
    fecha           DATE NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    reservada_por   VARCHAR(36),
    version         BIGINT DEFAULT 0,
    UNIQUE (consultorio_id, fecha, hora_inicio)
);
CREATE INDEX IF NOT EXISTS idx_slot_fecha_consultorio ON disponibilidad_slot(fecha, consultorio_id);

-- Trazabilidad de la máquina de estados
CREATE TABLE IF NOT EXISTS cita_estado_historial (
    id         BIGSERIAL PRIMARY KEY,
    cita_id    VARCHAR(36) NOT NULL REFERENCES cita(id),
    estado     VARCHAR(24) NOT NULL,
    actor      VARCHAR(36),
    canal      VARCHAR(12),
    timestamp  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_historial_cita ON cita_estado_historial(cita_id);

-- Outbox transaccional (integración por eventos)
CREATE TABLE IF NOT EXISTS outbox_event (
    id           BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL,
    tipo         VARCHAR(80) NOT NULL,
    payload      TEXT NOT NULL,
    status       VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    sent_at      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_event(status, created_at);

-- ---------------------------------------------------------------
-- Datos iniciales: especialidades (data-driven)
-- ---------------------------------------------------------------
INSERT INTO especialidad_tipo (id, codigo, nombre, requiere_remision, habilitada) VALUES
    (1, 'GENERAL',   'Consulta General',            FALSE, TRUE),
    (2, 'PEDIATRA',  'Pediatría',                   FALSE, TRUE),
    (3, 'CARDIO',    'Cardiología',                 TRUE,  TRUE),
    (4, 'OBSTETRA',  'Obstetricia',                 FALSE, TRUE),
    (5, 'VACUNACION','Vacunación',                  FALSE, TRUE),
    (6, 'ODONTOLOGIA','Odontología',                FALSE, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------
-- Datos iniciales: reglas Resolución 3280 (data-driven)
--  - R1: pacientes <6 años que asisten a Pediatría -> sugieren Vacunación (esquema)
--  - R2: mujer con >=2 hijos -> sugieren Obstetricia (control)
--  - R3: etnia INDIGENA -> sugieren programa especial
--  - R4: embarazo como riesgo -> sugieren Obstetricia / control prenatal
-- Estas reglas se editan desde una UI sin recompilar el servicio.
-- ---------------------------------------------------------------
INSERT INTO regla_3280 (especialidad_origen_id, condicion, tipo_cita_sugerida_id, tipo_cita_sugerida_nombre, dias_ventana, prioridad, version, habilitada) VALUES
    (2, '{"edad_lte": 6}',                                        5, 'Vacunación',       30, 1, 1, TRUE),
    (1, '{"num_hijos_gte": 2}',                                   4, 'Obstetricia',      60, 2, 1, TRUE),
    (1, '{"etnia_in": ["INDIGENA", "AFRO"]}',                     3, 'Cardiología',      90, 3, 1, TRUE),
    (1, '{"tiene_riesgo": "EMBARAZO"}',                           4, 'Obstetricia',      30, 1, 1, TRUE)
ON CONFLICT DO NOTHING;
