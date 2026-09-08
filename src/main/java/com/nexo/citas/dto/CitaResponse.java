package com.nexo.citas.dto;

import com.nexo.citas.domain.Cita;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta de una cita.
 */
public class CitaResponse {

    private String id;
    private String numero;
    private String pacienteId;
    private Long especialidadTipoId;
    private Long consultorioId;
    private String ubicacion;
    private LocalDateTime fecha;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String canal;
    private String estado;
    private List<SugerenciaResponse> sugerencias;

    // Campos de agenda/administrativos (integracion con nexo-personal-api)
    private Long agendaId;
    private Long sedeId;
    private String modalidad;
    private String funcionalidad;
    private String rotulo;
    private String observacionesAdministrativas;
    private Boolean turnoDoble;
    private Boolean agendaGrupal;
    private Boolean notificacionAutomatica;

    public CitaResponse() {
    }

    public static CitaResponse fromDomain(Cita c) {
        CitaResponse r = new CitaResponse();
        r.setId(c.getId());
        r.setNumero(c.getNumero());
        r.setPacienteId(c.getPacienteId());
        r.setEspecialidadTipoId(c.getEspecialidadTipoId());
        r.setConsultorioId(c.getConsultorioId());
        r.setUbicacion(c.getUbicacion());
        r.setFecha(c.getFecha());
        r.setHoraInicio(c.getHoraInicio());
        r.setHoraFin(c.getHoraFin());
        r.setCanal(c.getCanal().name());
        r.setEstado(c.getEstado().name());
        return r;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getEspecialidadTipoId() {
        return especialidadTipoId;
    }

    public void setEspecialidadTipoId(Long especialidadTipoId) {
        this.especialidadTipoId = especialidadTipoId;
    }

    public Long getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(Long consultorioId) {
        this.consultorioId = consultorioId;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<SugerenciaResponse> getSugerencias() {
        return sugerencias;
    }

    public void setSugerencias(List<SugerenciaResponse> sugerencias) {
        this.sugerencias = sugerencias;
    }

    public Long getAgendaId() { return agendaId; }
    public void setAgendaId(Long agendaId) { this.agendaId = agendaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getFuncionalidad() { return funcionalidad; }
    public void setFuncionalidad(String funcionalidad) { this.funcionalidad = funcionalidad; }
    public String getRotulo() { return rotulo; }
    public void setRotulo(String rotulo) { this.rotulo = rotulo; }
    public String getObservacionesAdministrativas() { return observacionesAdministrativas; }
    public void setObservacionesAdministrativas(String observacionesAdministrativas) { this.observacionesAdministrativas = observacionesAdministrativas; }
    public Boolean getTurnoDoble() { return turnoDoble; }
    public void setTurnoDoble(Boolean turnoDoble) { this.turnoDoble = turnoDoble; }
    public Boolean getAgendaGrupal() { return agendaGrupal; }
    public void setAgendaGrupal(Boolean agendaGrupal) { this.agendaGrupal = agendaGrupal; }
    public Boolean getNotificacionAutomatica() { return notificacionAutomatica; }
    public void setNotificacionAutomatica(Boolean notificacionAutomatica) { this.notificacionAutomatica = notificacionAutomatica; }
}
