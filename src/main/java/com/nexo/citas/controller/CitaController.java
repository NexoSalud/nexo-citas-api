package com.nexo.citas.controller;

import com.nexo.citas.domain.EstadoCita;
import com.nexo.citas.domain.PerfilPaciente;
import com.nexo.citas.dto.CitaResponse;
import com.nexo.citas.dto.CrearCitaRequest;
import com.nexo.citas.dto.DisponibilidadResponse;
import com.nexo.citas.dto.SugerenciaResponse;
import com.nexo.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /**
     * Crear cita (omnicanal: WEB | IVR | PRESENCIAL).
     * Evalua el motor 3280 y devuelve la cita creada + sugerencias preventivas.
     */
    @PostMapping
    public ResponseEntity<CitaResponse> crearCita(@Valid @RequestBody CrearCitaRequest request) {
        CitaResponse response = citaService.crearCita(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Sugerencias 3280 para un paciente (sin crear cita). Permite al frontend
     * mostrar el pipeline antes de confirmar.
     */
    @PostMapping("/sugerencias")
    public ResponseEntity<List<SugerenciaResponse>> sugerencias(
            @RequestParam Long especialidadTipoId,
            @RequestBody Map<String, Object> perfilBody) {
        PerfilPaciente perfil = new PerfilPaciente(
                num(perfilBody.get("edad")), str(perfilBody.get("etnia")), str(perfilBody.get("genero")),
                num(perfilBody.get("numHijos")), str(perfilBody.get("ubicacion")), null, null, null);
        List<SugerenciaResponse> sugerencias = citaService.sugerir(especialidadTipoId, perfil);
        return ResponseEntity.ok(sugerencias);
    }

    /**
     * Agregar con un clic: genera el siguiente segmento del pipeline 3280.
     */
    @PostMapping("/{id}/pipeline")
    public ResponseEntity<CitaResponse> crearSegmento(@PathVariable String id,
                                                      @RequestBody Map<String, Object> body) {
        Long tipoCita = Long.valueOf(String.valueOf(body.get("tipoCitaSugeridaId")));
        CitaResponse response = citaService.crearPipeline(id, tipoCita);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Transicionar la maquina de estados (integracion con Facturacion por eventos).
     */
    @PostMapping("/{id}/estado")
    public ResponseEntity<CitaResponse> transicionar(@PathVariable String id,
                                                     @RequestBody Map<String, Object> body) {
        EstadoCita estado = EstadoCita.valueOf(String.valueOf(body.get("estado")).toUpperCase());
        String causa = body.get("causa") != null ? String.valueOf(body.get("causa")) : null;
        String actor = body.get("actor") != null ? String.valueOf(body.get("actor")) : null;
        CitaResponse response = citaService.transicionar(id, estado, causa, actor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(citaService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CitaResponse>> listarPorPaciente(@RequestParam String pacienteId) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId));
    }

    /**
     * Disponibilidad real de una agenda medica en una fecha (integracion con
     * nexo-personal-api): cruza los slots crudos de la agenda con las citas
     * activas ya reservadas para marcar cuales siguen disponibles.
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<DisponibilidadResponse>> disponibilidad(@RequestParam Long agendaId,
                                                                       @RequestParam String fecha) {
        List<DisponibilidadResponse> response = citaService.getDisponibilidad(agendaId, LocalDate.parse(fecha));
        return ResponseEntity.ok(response);
    }

    private Integer num(Object o) {
        return o != null ? Integer.valueOf(String.valueOf(o)) : null;
    }

    private String str(Object o) {
        return o != null ? String.valueOf(o) : null;
    }
}
