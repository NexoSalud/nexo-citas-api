package com.nexo.citas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.client.MedicalAgendaDto;
import com.nexo.citas.client.PersonalApiClient;
import com.nexo.citas.client.SlotDto;
import com.nexo.citas.domain.Canal;
import com.nexo.citas.domain.Cita;
import com.nexo.citas.domain.EstadoCita;
import com.nexo.citas.domain.PerfilPaciente;
import com.nexo.citas.domain.reglas.CondicionEvaluador;
import com.nexo.citas.domain.reglas.Motor3280;
import com.nexo.citas.domain.reglas.Sugerencia;
import com.nexo.citas.dto.CitaResponse;
import com.nexo.citas.dto.CrearCitaRequest;
import com.nexo.citas.dto.DisponibilidadResponse;
import com.nexo.citas.dto.SugerenciaResponse;
import com.nexo.citas.entity.CitaEntity;
import com.nexo.citas.entity.CitaEstadoHistorialEntity;
import com.nexo.citas.entity.CitaPipelineEntity;
import com.nexo.citas.entity.DisponibilidadSlotEntity;
import com.nexo.citas.entity.EspecialidadTipoEntity;
import com.nexo.citas.event.TipoEvento;
import com.nexo.citas.outbox.OutboxPublisher;
import com.nexo.citas.repository.CitaEstadoHistorialRepository;
import com.nexo.citas.repository.CitaPipelineRepository;
import com.nexo.citas.repository.CitaRepository;
import com.nexo.citas.repository.DisponibilidadSlotRepository;
import com.nexo.citas.repository.EspecialidadTipoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Capa de aplicacion del contexto Citas. Orquesta el dominio (Cita, Motor3280,
 * maquina de estados) con la persistencia y los eventos de integracion (outbox).
 */
@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final CitaPipelineRepository pipelineRepository;
    private final CitaEstadoHistorialRepository historialRepository;
    private final DisponibilidadSlotRepository slotRepository;
    private final EspecialidadTipoRepository especialidadRepository;
    private final OutboxPublisher outboxPublisher;
    private final Motor3280 motor3280;
    private final ObjectMapper objectMapper;
    private final PersonalApiClient personalApiClient;

    public CitaService(CitaRepository citaRepository,
                       CitaPipelineRepository pipelineRepository,
                       CitaEstadoHistorialRepository historialRepository,
                       DisponibilidadSlotRepository slotRepository,
                       EspecialidadTipoRepository especialidadRepository,
                       OutboxPublisher outboxPublisher,
                       Motor3280 motor3280,
                       ObjectMapper objectMapper,
                       PersonalApiClient personalApiClient) {
        this.citaRepository = citaRepository;
        this.pipelineRepository = pipelineRepository;
        this.historialRepository = historialRepository;
        this.slotRepository = slotRepository;
        this.especialidadRepository = especialidadRepository;
        this.outboxPublisher = outboxPublisher;
        this.motor3280 = motor3280;
        this.objectMapper = objectMapper;
        this.personalApiClient = personalApiClient;
    }

    @Transactional
    public CitaResponse crearCita(CrearCitaRequest req) {
        esTipoValido(req.getEspecialidadTipoId());

        // Construir el perfil del paciente (evaluado por el motor 3280)
        PerfilPaciente perfil = aPerfil(req);

        // Rango horario (omnicanal: mismo caso de uso para todos los canales)
        LocalDateTime fecha = LocalDate.parse(req.getFecha()).atStartOfDay();
        LocalTime inicio = LocalTime.parse(req.getHoraInicio());
        int duracion = req.getDuracionMin() != null ? req.getDuracionMin() : 30;
        LocalTime fin = req.getHoraFin() != null ? LocalTime.parse(req.getHoraFin())
                : inicio.plusMinutes(duracion);
        if (fin.isBefore(inicio) || fin.equals(inicio)) {
            throw new IllegalArgumentException("horaFin debe ser posterior a horaInicio");
        }

        LocalDateTime inicioDt = fecha.with(inicio);
        LocalDateTime finDt = fecha.with(fin);

        Canal canal = Canal.from(req.getCanal());

        // Disponibilidad real de agenda (integracion con nexo-personal-api).
        // Solo aplica a citas que referencian una agenda medica; el flujo
        // omnicanal directo (sin agenda) mantiene su comportamiento actual.
        if (req.getAgendaId() != null) {
            validarAgenda(req, fecha, inicio, inicioDt);
        }

        // Asignacion de recurso: anti doble-book del consultorio
        reservarSlot(req, fecha.toLocalDate(), inicio, fin);

        // Construir agregado de dominio con la maquina de estados
        String id = UUID.randomUUID().toString();
        String numero = generarNumero();
        Cita cita = new Cita(id, numero, req.getPacienteId(), req.getUsuarioCreador(),
                req.getEspecialidadTipoId(), req.getConsultorioId(), req.getUbicacion(),
                fecha, inicioDt, finDt, canal, perfil, EstadoCita.CREADA);

        CitaEntity entidad = CitaEntity.fromDomain(cita, escribirPerfilJson(perfil));
        entidad.setMunicipio(req.getMunicipio());
        entidad.setCodigoDane(req.getCodigoDane());
        entidad.setTipoIdentificacion(req.getTipoIdentificacion());
        entidad.setRegimen(req.getRegimen());
        entidad.setAseguradora(req.getAseguradora());
        entidad.setDosis(req.getDosis());
        entidad.setVacuna(req.getVacuna());
        entidad.setAgendaId(req.getAgendaId());
        entidad.setSedeId(req.getSedeId());
        entidad.setModalidad(req.getModalidad());
        entidad.setFuncionalidad(req.getFuncionalidad());
        entidad.setRotulo(req.getRotulo());
        entidad.setObservacionesAdministrativas(req.getObservacionesAdministrativas());
        entidad.setTurnoDoble(req.getTurnoDoble() != null ? req.getTurnoDoble() : Boolean.FALSE);
        entidad.setAgendaGrupal(req.getAgendaGrupal() != null ? req.getAgendaGrupal() : Boolean.FALSE);
        entidad.setNotificacionAutomatica(req.getNotificacionAutomatica() != null ? req.getNotificacionAutomatica() : Boolean.TRUE);
        citaRepository.save(entidad);

        registrarHistorial(id, EstadoCita.CREADA, req.getUsuarioCreador(), canal);

        // Evento de integracion transaccional (outbox) -> Facturacion consume
        Map<String, Object> data = new HashMap<>();
        data.put("citaId", id);
        data.put("numero", numero);
        data.put("pacienteId", req.getPacienteId());
        data.put("importe", montoDe(req.getEspecialidadTipoId()));
        outboxPublisher.publicar(id, TipoEvento.CITACA_CREADA, data);

        CitaResponse respuesta = CitaResponse.fromDomain(cita);
        respuesta.setSugerencias(sugerir(req.getEspecialidadTipoId(), perfil));
        copiarCamposAdministrativos(respuesta, entidad);
        return respuesta;
    }

    /**
     * Motor 3280: evalua el perfil y devuelve las citas preventivas sugeridas (pipeline).
     */
    public List<SugerenciaResponse> sugerir(Long especialidadTipoId, PerfilPaciente perfil) {
        List<Sugerencia> sugerencias = motor3280.sugerir(especialidadTipoId, perfil);
        List<SugerenciaResponse> resultado = new ArrayList<>();
        for (Sugerencia s : sugerencias) {
            resultado.add(SugerenciaResponse.fromDomain(s));
        }
        return resultado;
    }

    /**
     * "Agregar con un clic": crea un pipeline secuencial de citas preventivas sugeridas.
     */
    @Transactional
    public CitaResponse crearPipeline(String citaOrigenId, Long citaHijaEspecialidadTipoId) {
        CitaEntity origen = citaRepository.findById(citaOrigenId)
                .orElseThrow(() -> new EntityNotFoundException("Cita origen no encontrada: " + citaOrigenId));

        if (!"CREADA".equals(origen.getEstado())) {
            throw new IllegalStateException("La cita origen debe estar en estado CREADA para generar el pipeline");
        }

        PerfilPaciente perfil = leerPerfilJson(origen.getPerfilPacienteSnapshot());
        EspecialidadTipoEntity tipo = especialidadRepository.findById(citaHijaEspecialidadTipoId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de cita sugerida no encontrado: " + citaHijaEspecialidadTipoId));

        // Nueva cita hija consecutiva (siguiente slot, mismo consultorio si existe)
        LocalDateTime inicioHija = origen.getHoraFin();
        Long consultorio = origen.getConsultorioId();
        Cita citaHija = new Cita(
                UUID.randomUUID().toString(),
                generarNumero(),
                origen.getPacienteId(),
                origen.getUsuarioCreador(),
                tipo.getId(),
                consultorio,
                origen.getUbicacion(),
                inicioHija.toLocalDate().atStartOfDay(),
                inicioHija,
                inicioHija.plusMinutes(30),
                Canal.from(origen.getCanal()),
                perfil,
                EstadoCita.CREADA);

        CitaEntity entidadHija = CitaEntity.fromDomain(citaHija, escribirPerfilJson(perfil));
        citaRepository.save(entidadHija);
        registrarHistorial(citaHija.getId(), EstadoCita.CREADA, origen.getUsuarioCreador(), Canal.from(origen.getCanal()));

        int orden = pipelineRepository.findByCitaOrigenIdOrderByOrdenAsc(citaOrigenId).size() + 1;
        CitaPipelineEntity segmento = new CitaPipelineEntity();
        segmento.setCitaOrigenId(citaOrigenId);
        segmento.setCitaHijaId(citaHija.getId());
        segmento.setOrden(orden);
        segmento.setEstado("PENDIENTE");
        pipelineRepository.save(segmento);

        Map<String, Object> data = new HashMap<>();
        data.put("citaOrigenId", citaOrigenId);
        data.put("citaHijaId", citaHija.getId());
        data.put("especialidad", tipo.getNombre());
        outboxPublisher.publicar(citaHija.getId(), TipoEvento.PIPELINE_SUGERIDO, data);

        return CitaResponse.fromDomain(citaHija);
    }

    /**
     * Transicion de la maquina de estados con integracion por eventos.
     */
    @Transactional
    public CitaResponse transicionar(String id, EstadoCita nuevo, String causa, String actor) {
        CitaEntity entidad = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + id));

        PerfilPaciente perfil = leerPerfilJson(entidad.getPerfilPacienteSnapshot());
        Cita cita = new Cita(
                entidad.getId(), entidad.getNumero(), entidad.getPacienteId(), entidad.getUsuarioCreador(),
                entidad.getEspecialidadTipoId(), entidad.getConsultorioId(), entidad.getUbicacion(),
                entidad.getFecha(), entidad.getHoraInicio(), entidad.getHoraFin(),
                Canal.from(entidad.getCanal()), perfil, EstadoCita.valueOf(entidad.getEstado()));

        cita.transicionar(nuevo, causa); // valida transiciones validas

        entidad.setEstado(cita.getEstado().name());
        entidad.setCausaCancelacion(cita.getCausaCancelacion());
        citaRepository.save(entidad);
        registrarHistorial(id, nuevo, actor, Canal.from(entidad.getCanal()));

        TipoEvento evento = eventoDeTransicion(nuevo);
        Map<String, Object> data = new HashMap<>();
        data.put("citaId", id);
        data.put("estado", nuevo.name());
        data.put("causa", causa);
        outboxPublisher.publicar(id, evento, data);

        CitaResponse respuesta = CitaResponse.fromDomain(cita);
        copiarCamposAdministrativos(respuesta, entidad);
        return respuesta;
    }

    @Transactional(readOnly = true)
    public CitaResponse getById(String id) {
        CitaEntity entidad = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + id));
        PerfilPaciente perfil = leerPerfilJson(entidad.getPerfilPacienteSnapshot());
        Cita cita = new Cita(
                entidad.getId(), entidad.getNumero(), entidad.getPacienteId(), entidad.getUsuarioCreador(),
                entidad.getEspecialidadTipoId(), entidad.getConsultorioId(), entidad.getUbicacion(),
                entidad.getFecha(), entidad.getHoraInicio(), entidad.getHoraFin(),
                Canal.from(entidad.getCanal()), perfil, EstadoCita.valueOf(entidad.getEstado()));
        CitaResponse respuesta = CitaResponse.fromDomain(cita);
        copiarCamposAdministrativos(respuesta, entidad);
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listarPorPaciente(String pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaDesc(pacienteId).stream()
                .map(e -> {
                    PerfilPaciente p = leerPerfilJson(e.getPerfilPacienteSnapshot());
                    CitaResponse respuesta = CitaResponse.fromDomain(new Cita(
                            e.getId(), e.getNumero(), e.getPacienteId(), e.getUsuarioCreador(),
                            e.getEspecialidadTipoId(), e.getConsultorioId(), e.getUbicacion(),
                            e.getFecha(), e.getHoraInicio(), e.getHoraFin(),
                            Canal.from(e.getCanal()), p, EstadoCita.valueOf(e.getEstado())));
                    copiarCamposAdministrativos(respuesta, e);
                    return respuesta;
                })
                .toList();
    }

    /**
     * Disponibilidad de una agenda medica en una fecha: cruza los slots crudos
     * de nexo-personal-api con las citas activas ya reservadas en esa agenda.
     */
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> getDisponibilidad(Long agendaId, LocalDate fecha) {
        List<SlotDto> slots;
        try {
            slots = personalApiClient.getAvailableSlots(agendaId, fecha);
        } catch (RestClientException ex) {
            throw new EntityNotFoundException(
                    "No fue posible consultar la disponibilidad de la agenda " + agendaId
                            + " en nexo-personal-api: " + ex.getMessage());
        }

        List<CitaEntity> activas = citaRepository.findActivasByAgendaAndFecha(agendaId, fecha.atStartOfDay());
        Set<LocalTime> ocupadas = new HashSet<>();
        for (CitaEntity c : activas) {
            ocupadas.add(c.getHoraInicio().toLocalTime());
        }

        List<DisponibilidadResponse> resultado = new ArrayList<>();
        for (SlotDto slot : slots) {
            boolean disponible = true;
            if (slot.getStartTime() != null) {
                disponible = !ocupadas.contains(LocalTime.parse(slot.getStartTime()));
            }
            resultado.add(new DisponibilidadResponse(slot.getStartTime(), slot.getEndTime(), disponible));
        }
        return resultado;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void esTipoValido(Long especialidadTipoId) {
        especialidadRepository.findById(especialidadTipoId)
                .filter(EspecialidadTipoEntity::getHabilitada)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de especialidad no valido/deshabilitado: " + especialidadTipoId));
    }

    private void reservarSlot(CrearCitaRequest req, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (req.getConsultorioId() != null) {
            // El slot debe existir y estar libre
            if (slotRepository.existsByConsultorioIdAndFechaAndHoraInicio(req.getConsultorioId(), fecha, inicio)) {
                throw new IllegalStateException("Slot duplicado: consultorio " + req.getConsultorioId()
                        + " en " + fecha + " " + inicio);
            }
        } else {
            // Sin consultorio explicito: validar que no haya solapamiento con citas activas
            LocalDateTime ini = fecha.atTime(inicio);
            LocalDateTime f = fecha.atTime(fin);
            if (!citaRepository.findByRango(req.getEspecialidadTipoId(), ini, f).isEmpty()) {
                throw new IllegalStateException("Conflicto de disponibilidad en el rango solicitado");
            }
        }
    }

    private String generarNumero() {
        return "CIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Valida disponibilidad real de la agenda medica (nexo-personal-api) antes
     * de crear una cita: agenda existente, fecha dentro de vigencia, dia
     * habilitado, hora dentro del horario, agenda ABIERTA y, si no es una
     * agenda grupal, que el slot puntual no este ya ocupado por otra cita activa.
     */
    private void validarAgenda(CrearCitaRequest req, LocalDateTime fecha, LocalTime inicio, LocalDateTime inicioDt) {
        Long agendaId = req.getAgendaId();
        MedicalAgendaDto agenda;
        try {
            agenda = personalApiClient.getAgenda(agendaId)
                    .orElseThrow(() -> new EntityNotFoundException("Agenda no encontrada: " + agendaId));
        } catch (RestClientException ex) {
            throw new EntityNotFoundException(
                    "No fue posible consultar la agenda " + agendaId
                            + " en nexo-personal-api: " + ex.getMessage());
        }

        LocalDate fechaCita = fecha.toLocalDate();
        try {
            LocalDate inicioAgenda = agenda.getStartDate() != null ? LocalDate.parse(agenda.getStartDate()) : null;
            LocalDate finAgenda = agenda.getEndDate() != null ? LocalDate.parse(agenda.getEndDate()) : null;
            if ((inicioAgenda != null && fechaCita.isBefore(inicioAgenda))
                    || (finAgenda != null && fechaCita.isAfter(finAgenda))) {
                throw new IllegalStateException("La fecha " + fechaCita
                        + " esta fuera del rango vigente de la agenda " + agendaId);
            }

            DayOfWeek diaCita = fechaCita.getDayOfWeek();
            boolean diaValido = agenda.getWorkDays() != null && agenda.getWorkDays().stream()
                    .anyMatch(codigo -> diaCita.equals(mapDiaSemana(codigo)));
            if (!diaValido) {
                throw new IllegalStateException("El dia " + diaCita
                        + " no esta habilitado en la agenda " + agendaId);
            }

            LocalTime aperturaAgenda = agenda.getStartTime() != null ? LocalTime.parse(agenda.getStartTime()) : null;
            LocalTime cierreAgenda = agenda.getEndTime() != null ? LocalTime.parse(agenda.getEndTime()) : null;
            if (aperturaAgenda == null || cierreAgenda == null
                    || inicio.isBefore(aperturaAgenda) || !inicio.isBefore(cierreAgenda)) {
                throw new IllegalStateException("La hora " + inicio
                        + " esta fuera del horario de la agenda " + agendaId);
            }
        } catch (DateTimeParseException ex) {
            throw new IllegalStateException("La agenda " + agendaId + " tiene datos de vigencia/horario invalidos", ex);
        }

        if (!"ABIERTA".equalsIgnoreCase(agenda.getAgendaState())) {
            throw new IllegalStateException("La agenda " + agendaId
                    + " no esta ABIERTA (estado actual: " + agenda.getAgendaState() + ")");
        }

        boolean esGrupal = Boolean.TRUE.equals(req.getAgendaGrupal());
        if (!esGrupal) {
            boolean ocupado = citaRepository.findActivasByAgendaAndFecha(agendaId, fecha).stream()
                    .anyMatch(c -> inicioDt.equals(c.getHoraInicio()));
            if (ocupado) {
                throw new IllegalStateException("El slot seleccionado ya no esta disponible");
            }
        }
    }

    private DayOfWeek mapDiaSemana(String codigo) {
        if (codigo == null) {
            return null;
        }
        switch (codigo.trim().toUpperCase()) {
            case "L": return DayOfWeek.MONDAY;
            case "M": return DayOfWeek.TUESDAY;
            case "X": return DayOfWeek.WEDNESDAY;
            case "J": return DayOfWeek.THURSDAY;
            case "V": return DayOfWeek.FRIDAY;
            case "S": return DayOfWeek.SATURDAY;
            case "D": return DayOfWeek.SUNDAY;
            default: return null;
        }
    }

    /**
     * Copia los metadatos administrativos/de agenda (no forman parte de la
     * maquina de estados del dominio) desde la entidad hacia la respuesta.
     */
    private void copiarCamposAdministrativos(CitaResponse respuesta, CitaEntity entidad) {
        respuesta.setAgendaId(entidad.getAgendaId());
        respuesta.setSedeId(entidad.getSedeId());
        respuesta.setModalidad(entidad.getModalidad());
        respuesta.setFuncionalidad(entidad.getFuncionalidad());
        respuesta.setRotulo(entidad.getRotulo());
        respuesta.setObservacionesAdministrativas(entidad.getObservacionesAdministrativas());
        respuesta.setTurnoDoble(entidad.getTurnoDoble());
        respuesta.setAgendaGrupal(entidad.getAgendaGrupal());
        respuesta.setNotificacionAutomatica(entidad.getNotificacionAutomatica());
    }

    private void registrarHistorial(String citaId, EstadoCita estado, String actor, Canal canal) {
        CitaEstadoHistorialEntity h = new CitaEstadoHistorialEntity();
        h.setCitaId(citaId);
        h.setEstado(estado.name());
        h.setActor(actor);
        h.setCanal(canal.name());
        h.setTimestamp(LocalDateTime.now());
        historialRepository.save(h);
    }

    private Double montoDe(Long especialidadTipoId) {
        // Integracion por eventos con Facturacion: se envía el contexto de la cita.
        return 0.0; // monto/codigo de tarifa lo resuelve Facturacion.
    }

    private TipoEvento eventoDeTransicion(EstadoCita nuevo) {
        switch (nuevo) {
            case FACTURADA: return TipoEvento.CITA_FACTURADA;
            case COMPLETADA: return TipoEvento.CITA_COMPLETADA;
            case APLICADA: return TipoEvento.CITA_APLICADA;
            case NO_ASISTIO: return TipoEvento.CITA_NO_ASISTIO;
            case CANCELADA_USUARIO: return TipoEvento.CITA_CANCELADA;
            case CANCELADA: return TipoEvento.CITA_CANCELADA;
            case REAGENDADA_SISTEMA: return TipoEvento.CITA_REAGENDADA;
            default: return TipoEvento.CITACA_CREADA;
        }
    }

    private PerfilPaciente aPerfil(CrearCitaRequest req) {
        return new PerfilPaciente(
                req.getEdad(), req.getEtnia(), req.getGenero(), req.getNumHijos(),
                req.getUbicacionPaciente(), req.getAfecciones(), req.getRiesgos(),
                req.getPerfilExtra());
    }

    private String escribirPerfilJson(PerfilPaciente perfil) {
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("edad", perfil.getEdad());
            m.put("etnia", perfil.getEtnia());
            m.put("genero", perfil.getGenero());
            m.put("numHijos", perfil.getNumHijos());
            m.put("ubicacion", perfil.getUbicacion());
            m.put("afecciones", perfil.getAfecciones());
            m.put("riesgos", perfil.getRiesgos());
            m.put("extra", perfil.getExtra());
            return objectMapper.writeValueAsString(m);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo serializar el perfil", e);
        }
    }

    private PerfilPaciente leerPerfilJson(String json) {
        if (json == null || json.isBlank()) {
            return new PerfilPaciente(null, null, null, null, null, null, null, null);
        }
        try {
            var node = objectMapper.readTree(json);
            return new PerfilPaciente(
                    node.hasNonNull("edad") ? node.get("edad").asInt() : null,
                    node.hasNonNull("etnia") ? node.get("etnia").asText() : null,
                    node.hasNonNull("genero") ? node.get("genero").asText() : null,
                    node.hasNonNull("numHijos") ? node.get("numHijos").asInt() : null,
                    node.hasNonNull("ubicacion") ? node.get("ubicacion").asText() : null,
                    node.hasNonNull("afecciones") ? objectMapper.convertValue(node.get("afecciones"),
                            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}) : null,
                    node.hasNonNull("riesgos") ? objectMapper.convertValue(node.get("riesgos"),
                            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}) : null,
                    node.hasNonNull("extra") ? objectMapper.convertValue(node.get("extra"),
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}) : null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Perfil JSON invalido", e);
        }
    }
}
