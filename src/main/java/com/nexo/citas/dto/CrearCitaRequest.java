package com.nexo.citas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Request de creacion de cita (omnicanal: WEB | IVR | PRESENCIAL).
 * Los campos del perfil del paciente son la entrada para el motor 3280.
 */
public class CrearCitaRequest {

    @NotBlank(message = "Paciente ID is required")
    private String pacienteId;

    private String usuarioCreador;

    @NotNull(message = "Specialty type ID is required")
    private Long especialidadTipoId;

    private Long consultorioId;

    private String ubicacion; // {piso, ala}

    @NotBlank(message = "fecha is required")
    private String fecha;     // ISO-8601 fecha de la cita

    @NotBlank(message = "horaInicio is required")
    private String horaInicio; // HH:mm

    private String horaFin;    // HH:mm (opcional, si no se deriva de duracion)
    private Integer duracionMin;

    private String canal;      // WEB | IVR | PRESENCIAL (default PRESENCIAL)

    private Integer edad;
    private String etnia;
    private String genero;
    private Integer numHijos;
    private String ubicacionPaciente;
    private List<String> afecciones;
    private List<String> riesgos;
    private Map<String, Object> perfilExtra;

    // Campos de vacunacion/reporte PAI (opcionales)
    private String municipio;
    private String codigoDane;
    private String tipoIdentificacion;
    private String regimen;
    private String aseguradora;
    private String dosis;
    private String vacuna;

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

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(Integer duracionMin) {
        this.duracionMin = duracionMin;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getEtnia() {
        return etnia;
    }

    public void setEtnia(String etnia) {
        this.etnia = etnia;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getNumHijos() {
        return numHijos;
    }

    public void setNumHijos(Integer numHijos) {
        this.numHijos = numHijos;
    }

    public String getUbicacionPaciente() {
        return ubicacionPaciente;
    }

    public void setUbicacionPaciente(String ubicacionPaciente) {
        this.ubicacionPaciente = ubicacionPaciente;
    }

    public List<String> getAfecciones() {
        return afecciones;
    }

    public void setAfecciones(List<String> afecciones) {
        this.afecciones = afecciones;
    }

    public List<String> getRiesgos() {
        return riesgos;
    }

    public void setRiesgos(List<String> riesgos) {
        this.riesgos = riesgos;
    }

    public Map<String, Object> getPerfilExtra() {
        return perfilExtra;
    }

    public void setPerfilExtra(Map<String, Object> perfilExtra) {
        this.perfilExtra = perfilExtra;
    }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getCodigoDane() { return codigoDane; }
    public void setCodigoDane(String codigoDane) { this.codigoDane = codigoDane; }
    public String getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(String tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }
    public String getRegimen() { return regimen; }
    public void setRegimen(String regimen) { this.regimen = regimen; }
    public String getAseguradora() { return aseguradora; }
    public void setAseguradora(String aseguradora) { this.aseguradora = aseguradora; }
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    public String getVacuna() { return vacuna; }
    public void setVacuna(String vacuna) { this.vacuna = vacuna; }

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
