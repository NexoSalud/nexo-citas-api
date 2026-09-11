# nexo-citas-api

Microservicio de agendamiento de citas medicas y reglas preventivas 3280.
Usa Java 17, Spring Boot 3.2.5, PostgreSQL y Flyway.

## Contrato HTTP

Todos los endpoints se publican mediante KrakenD bajo `/api/v1/appointments`:

- `POST /` crea una cita y devuelve sugerencias 3280.
- `GET /?pacienteId=...` lista las citas de un paciente.
- `GET /{id}` consulta una cita.
- `POST /sugerencias?especialidadTipoId=...` calcula sugerencias sin crear.
- `POST /{id}/pipeline` crea el siguiente segmento preventivo.
- `POST /{id}/estado` transiciona el estado.
- `GET /disponibilidad?agendaId=...&fecha=YYYY-MM-DD` consulta slots.

El frontend consume estos recursos mediante `/api/proxy/citas/...`; no debe
llamar al gateway directamente desde el navegador.

## Desarrollo local

```bash
mvn -s settings.xml test
```

El servicio escucha en `8087`. La base por defecto es `citas_db`. El gateway
debe apuntar a `CITAS_SERVICE_HOST=http://localhost:8087`.

## Cache

Las lecturas de citas por paciente, cita individual y disponibilidad usan
Caffeine. Las escrituras invalidan las entradas para evitar datos obsoletos.
Configura `CITAS_CACHE_TTL_SECONDS` y `CITAS_CACHE_MAX_SIZE` segun el entorno.

## Coolify

Usa `docker-compose.yml` como recurso Docker Compose o publica la imagen
`ghcr.io/nexosalud/nexo-citas-api:coolify`. El contenedor expone `8087` y su
health check es `/actuator/health`.

Variables minimas: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`PERSONAL_SERVICE_URL`, `CITAS_SERVICE_HOST` en el gateway y los valores de
Keycloak. Para construir desde el Dockerfile, entrega `GITHUB_ACTOR` y
`GITHUB_TOKEN` como build args para resolver `nexo-core-spring`.
