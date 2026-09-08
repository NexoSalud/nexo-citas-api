package com.nexo.citas.entity;

import com.nexo.core.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

/**
 * JPA Entity del agregado Cita.
 * Hereda campos de auditoria de AuditableEntity.
 */
@Entity
@Table(name = "cita")
public class CitaEntity extends AuditableEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "numero", length = 32, nullable = false, unique = true)
    private String numero;

    @Column(name = "paciente_id", length = 36, nullable = false)
    private String pacienteId;

    @Column(name = "usuario_creador", length = 36)
    private String usuarioCreador;

    @Column(name = "especialidad_tipo_id")
    private Long especialidadTipoId;

    @Column(name = "consultorio_id")
    private Long consultorioId;

    @Column(name = "ubicacion", length = 500)
    private String ubicacion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin;

    @Column(name = "canal", length = 12, nullable = false)
    private String canal;

    @Column(name = "perfil_paciente_snapshot", columnDefinition = "text")
    private String perfilPacienteSnapshot;

    @Column(name = "estado", length = 24, nullable = false)
    private String estado;

    @Column(name = "causa_cancelacion", length = 64)
    private String causaCancelacion;

    @Column(name = "municipio", length = 120)
    private String municipio;

    @Column(name = "codigo_dane", length = 8)
    private String codigoDane;

    @Column(name = "tipo_identificacion", length = 60)
    private String tipoIdentificacion;

    @Column(name = "regimen", length = 60)
    private String regimen;

    @Column(name = "aseguradora", length = 120)
    private String aseguradora;

    @Column(name = "dosis", length = 60)
    private String dosis;

    @Column(name = "vacuna", length = 120)
    private String vacuna;

    @Column(name = "agenda_id")
    private Long agendaId;

    @Column(name = "sede_id")
    private Long sedeId;

    @Column(name = "modalidad", length = 20)
    private String modalidad;

    @Column(name = "funcionalidad", length = 60)
    private String funcionalidad;

    @Column(name = "rotulo", length = 120)
    private String rotulo;

    @Column(name = "observaciones_administrativas", columnDefinition = "text")
    private String observacionesAdministrativas;

    @Column(name = "turno_doble")
    private Boolean turnoDoble;

    @Column(name = "agenda_grupal")
    private Boolean agendaGrupal;

    @Column(name = "notificacion_automatica")
    private Boolean notificacionAutomatica;

    @Version
    @Column(name = "version")
    private Long version;

    public CitaEntity() {
    }

    public static CitaEntity fromDomain(com.nexo.citas.domain.Cita cita, String perfilJson) {
        CitaEntity e = new CitaEntity();
        e.id = cita.getId();
        e.numero = cita.getNumero();
        e.pacienteId = cita.getPacienteId();
        e.usuarioCreador = cita.getUsuarioCreador();
        e.especialidadTipoId = cita.getEspecialidadTipoId();
        e.consultorioId = cita.getConsultorioId();
        e.ubicacion = cita.getUbicacion();
        e.fecha = cita.getFecha();
        e.horaInicio = cita.getHoraInicio();
        e.horaFin = cita.getHoraFin();
        e.canal = cita.getCanal().name();
        e.perfilPacienteSnapshot = perfilJson;
        e.estado = cita.getEstado().name();
        e.causaCancelacion = cita.getCausaCancelacion();
        return e;
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

    public String getPerfilPacienteSnapshot() {
        return perfilPacienteSnapshot;
    }

    public void setPerfilPacienteSnapshot(String perfilPacienteSnapshot) {
        this.perfilPacienteSnapshot = perfilPacienteSnapshot;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCausaCancelacion() {
        return causaCancelacion;
    }

    public void setCausaCancelacion(String causaCancelacion) {
        this.causaCancelacion = causaCancelacion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
