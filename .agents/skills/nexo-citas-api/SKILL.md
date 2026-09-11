---
name: nexo-citas-api
description: >-
  Mantener, depurar, extender y desplegar el modulo de citas de Nexo 2.0
  (nexo-citas-api + frontend-citas + KrakenD). Usar cuando una peticion
  mencione citas, appointments, agendamiento, disponibilidad, agendas
  medicas, reglas 3280, pipeline preventivo, estados de cita, cache o Coolify
  para este modulo.
---

# Nexo 2.0 - Skill del modulo de citas

Esta skill contiene el contexto operativo especifico de `nexo-citas-api`.
Su objetivo es reducir exploracion repetida, evitar asumir un CRUD generico y
validar el camino completo: frontend -> proxy Next.js -> KrakenD -> API ->
PostgreSQL/Personal.

## Alcance y repositorios

- Backend: `repos/nexo-citas-api`.
- Frontend: `repos/Frontend/apps/frontend-citas`.
- Gateway: `repos/gateway`.
- Orquestador local: `./nexo-dev` desde la raiz `/home/jhordy/nexo/v3`.
- Documentacion transversal: `.agents/skills/nexo-modulos-api`,
  `.agents/skills/nexo-frontend`, `.agents/skills/nexo-infrastructure`.

No ampliar la investigacion a otros modulos salvo que una dependencia directa
bloquee citas, especialmente `nexo-personal-api`, `gateway` o `@nexo/patients`.

## Contrato funcional real

El backend expone `/api/v1/appointments`:

| Metodo | Ruta | Contrato |
|---|---|---|
| POST | `/` | Crea cita y devuelve sugerencias 3280. |
| GET | `/?pacienteId=...` | Lista citas del paciente. `pacienteId` es obligatorio. |
| GET | `/{id}` | Consulta una cita. |
| POST | `/sugerencias?especialidadTipoId=...` | Calcula sugerencias sin crear cita. |
| POST | `/{id}/pipeline` | Crea el siguiente segmento preventivo. |
| POST | `/{id}/estado` | Ejecuta una transicion de estado. |
| GET | `/disponibilidad?agendaId=...&fecha=YYYY-MM-DD` | Consulta slots de Personal y cruza ocupacion local. |

El dominio principal esta compuesto por `Cita`, `CitaEntity`, `CitaService`,
`Motor3280`, reglas data-driven, historial, pipeline y outbox. Antes de
cambiar un contrato, leer el controlador, servicio, DTO, repositorio y
migraciones relacionadas.

### Reglas de negocio que no se deben perder

- La cita inicia normalmente en estado `CREADA`.
- La disponibilidad de una agenda se valida contra `nexo-personal-api`.
- Las relaciones con otros modulos se representan con IDs; no crear FK JPA a
  tablas de otra base de datos.
- Las citas activas deben impedir doble reserva.
- Los cambios relevantes publican eventos mediante outbox.
- Las excepciones de negocio deben responder 400/409, no 500.
- El gateway valida JWT y propaga `X-User-Id` y `X-User-Roles`; el backend no
  debe duplicar esa responsabilidad sin una razon documentada.

## Superficies que siempre deben permanecer alineadas

El puerto estandar del modulo es `8087`. Verificar los cuatro puntos:

1. `src/main/resources/application.yml` (`server.port`).
2. `docker-compose.yml` (`ports`, `SERVER_PORT`, healthcheck).
3. `Dockerfile` (`EXPOSE`, healthcheck).
4. `repos/gateway/config/templates/citas.tmpl` y `CITAS_SERVICE_HOST`.

El frontend nunca llama a `http://localhost:8080` desde el navegador. Usa:

```text
/api/proxy/citas/...
```

La ruta proxy lee la cookie de sesion, conserva query strings y llama a
`GATEWAY_URL` o `NEXT_PUBLIC_GATEWAY_URL`. En despliegues server-side de
Vercel/Coolify, `GATEWAY_URL` debe ser alcanzable desde el contenedor/runtime,
no una URL localhost del navegador.

Los endpoints GET del gateway deben conservar `querystring_params` e
`input_query_strings` para no perder `pacienteId`, `agendaId` o `fecha`.

## Flujo obligatorio para cada peticion

### 1. Definir una hipotesis local

Antes de editar, identificar:

- el comportamiento roto o solicitado;
- el archivo que decide ese comportamiento;
- una comprobacion barata que pueda refutar la hipotesis.

No empezar con una exploracion amplia. Leer primero el controlador, servicio,
cliente o proxy mas cercano al fallo.

### 2. Leer el contexto minimo

Para backend, normalmente:

```text
CitaController -> CitaService -> Repository/PersonalApiClient
DTO -> Entity -> db/migration
application.yml -> Dockerfile/docker-compose.yml
```

Para frontend:

```text
lib/citas.ts -> componente consumidor -> app/api/proxy/citas/[[...path]]/route.ts
```

Para gateway:

```text
config/templates/citas.tmpl -> config/krakend.tmpl -> docker-compose.yml
```

### 3. Hacer el cambio mas pequeno

Preservar nombres publicos y contratos existentes. No reemplazar el modelo
rico de citas por un CRUD generico `Appointment`.

### 4. Validar inmediatamente la misma superficie

Usar desde la raiz del workspace:

```bash
# Backend
mvn -s repos/nexo-citas-api/settings.xml \
  -f repos/nexo-citas-api/pom.xml test

# Frontend
cd repos/Frontend
pnpm --filter frontend-citas type-check
pnpm --filter frontend-citas lint
pnpm --filter frontend-citas build

# Coolify
cd repos/nexo-citas-api
docker compose config -q
```

Si el entorno no permite una validacion por version o red, registrar la
limitacion y ejecutar al menos una validacion estatica equivalente.

### 5. Verificar el camino de integracion

Con el ambiente levantado:

```bash
cd /home/jhordy/nexo/v3
./nexo-dev doctor
./nexo-dev up nexo-citas-api gateway frontend-citas
./nexo-dev status
./nexo-dev logs nexo-citas-api
```

Sin JWT, una ruta protegida del gateway debe responder `401`; eso demuestra
que la ruta existe y que KrakenD esta aplicando autenticacion. No confundir
`401` con un backend caido.

## Cache y consumo de recursos

La cache solo debe aplicarse a lecturas idempotentes y con invalidacion clara.

### Backend

- Caffeine local para consultas por paciente, cita individual y disponibilidad.
- TTL y tamano configurables mediante `CITAS_CACHE_TTL_SECONDS` y
  `CITAS_CACHE_MAX_SIZE`.
- Toda operacion que crea, genera pipeline o transiciona estado debe invalidar
  las entradas afectadas.
- No cachear respuestas dependientes del usuario si la clave no incluye el
  contexto de seguridad.
- No cachear errores de integraciones externas.

### Frontend

- Deduplicar GET concurrentes y usar TTL breve para evitar llamadas repetidas
  durante renders o navegacion.
- Limpiar cache despues de POST/PUT/PATCH/DELETE.
- Mantener el proxy como unico punto de salida; la cache no debe exponer JWT.

### Evitar consumo innecesario de contexto

- Leer solo los archivos de la ruta que decide el comportamiento.
- Usar la documentacion Markdown del modulo antes de inspeccionar otros repos.
- No cargar `node_modules`, `.next` ni `target` en busquedas.
- Reutilizar este contrato en vez de redescubrir endpoints en cada peticion.

## Checklist de Coolify

### Backend

- Imagen: `ghcr.io/nexosalud/nexo-citas-api:<tag>` o `docker-compose.yml`.
- Puerto publicado: `8087`.
- Health check: `GET /actuator/health`.
- PostgreSQL separado por modulo, base `citas_db`.
- Variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
  `PERSONAL_SERVICE_URL`, `KEYCLOAK_HOST`, `KEYCLOAK_REALM`,
  `CITAS_CACHE_TTL_SECONDS`, `CITAS_CACHE_MAX_SIZE`.
- Si Coolify construye la imagen: build args `GITHUB_ACTOR` y `GITHUB_TOKEN`
  para resolver `nexo-core-spring`.
- En produccion usar migraciones Flyway y `ddl-auto=validate`.

### Gateway

- `CITAS_SERVICE_HOST` apunta al nombre DNS/servicio real y al puerto `8087`.
- La plantilla `citas.tmpl` esta incluida en `krakend.tmpl`.
- GET conserva todos los query strings.
- Reiniciar o reconstruir el gateway despues de cambiar plantillas.

### Frontend

- Root directory: `apps/frontend-citas`.
- Node.js >= 20.9.
- `NEXT_PUBLIC_APP_BASE_URL` apunta al dominio de citas.
- `GATEWAY_URL` apunta a la URL publica o red interna alcanzable por el
  runtime server-side.
- Keycloak tiene registrado `/api/auth/callback` del dominio de citas.

## Diagnostico rapido

| Sintoma | Comprobacion prioritaria |
|---|---|
| 502 desde frontend | `GATEWAY_URL`, `CITAS_SERVICE_HOST`, puerto 8087 y healthcheck. |
| 401 inesperado | Cookie de sesion, issuer/client ID y configuracion JWT del gateway. |
| Filtros ignorados | `querystring_params`, `input_query_strings` y URL del proxy. |
| Cita duplicada | Consulta de solapamiento, estado activo, bloqueo/version y migracion. |
| Disponibilidad vacia | `PERSONAL_SERVICE_URL`, ruta de slots, fecha y agenda. |
| Build frontend falla | Node >=20.9, `pnpm install`, type-check y configuracion ESLint. |
| Backend no inicia | PostgreSQL, Flyway, `nexo-core-spring`, `SERVER_PORT` y variables DB. |

## Refinamiento continuo de la skill

Cada peticion nueva sobre citas debe mejorar esta skill cuando revele un
patron reutilizable:

1. Registrar el sintoma y la causa real, sin incluir secretos ni datos de
   pacientes.
2. Convertir la causa en una regla, diagnostico o comando reproducible.
3. Actualizar el contrato, checklist o tabla de diagnostico correspondiente.
4. Anotar la fecha y el cambio en el historial siguiente.
5. Revalidar el YAML frontmatter y los comandos afectados.

### Historial de refinamientos

- `2026-09-11`: version inicial creada despues de reparar el modulo. Incluye
  el contrato real de citas, alineacion de puerto `8087`, proxy Next.js,
  cache con invalidacion, validaciones y despliegue Coolify.

No almacenar aqui tokens, contrasenas, JWT, pacientes ni dumps de produccion.
