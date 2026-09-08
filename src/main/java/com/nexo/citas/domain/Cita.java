package com.nexo.citas.domain;

import java.time.LocalDateTime;

/**
 * AGREGADO RAIZ del Core Domain "Citas".
 *
 * Encapsula la maquina de estados de la cita y las invariantes de dominio.
 * Es un objeto de dominio "rico": no conoce el framework de persistencia.
 * La persistencia se hace a traves de la JPA Entity (ver paquete entity),
 * que utiliza el id para mapear el mismo registro.
 */
public class Cita {

    private final String id;
    private final String numero;
    private final String pacienteId;
    private final String usuarioCreador;
    private final Long especialidadTipoId;
    private final Long consultorioId;
    private final String ubicacion;      // JSON snapshot {piso, ala}
    private final LocalDateTime fecha;
    private final LocalDateTime horaInicio;
    private final LocalDateTime horaFin;
    private final Canal canal;
    private final PerfilPaciente perfilPaciente;

    private EstadoCita estado;
    private String causaCancelacion;

    public Cita(String id, String numero, String pacienteId, String usuarioCreador,
                Long especialidadTipoId, Long consultorioId, String ubicacion,
                LocalDateTime fecha, LocalDateTime horaInicio, LocalDateTime horaFin,
                Canal canal, PerfilPaciente perfilPaciente, EstadoCita estado) {
        this.id = id;
        this.numero = numero;
        this.pacienteId = pacienteId;
        this.usuarioCreador = usuarioCreador;
        this.especialidadTipoId = especialidadTipoId;
        this.consultorioId = consultorioId;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.canal = canal;
        this.perfilPaciente = perfilPaciente;
        this.estado = estado != null ? estado : EstadoCita.CREADA;
    }

    /**
     * Metodo de dominio que ejecuta una transicion respetando la maquina de estados.
     */
    public void transicionar(EstadoCita nuevo, String causa) {
        if (!EstadoCita.puedeTransicionar(this.estado, nuevo)) {
            throw new IllegalStateException(
                    "Transicion invalida de cita " + estado + " -> " + nuevo
                            + " (cita " + id + ")");
        }
        this.estado = nuevo;
        if (nuevo == EstadoCita.CANCELADA || nuevo == EstadoCita.CANCELADA_USUARIO) {
            this.causaCancelacion = causa;
        }
    }

    public boolean estaActiva() {
        return estado == EstadoCita.CREADA || estado == EstadoCita.FACTURADA
                || estado == EstadoCita.REAGENDADA_SISTEMA;
    }

    public String getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public Long getEspecialidadTipoId() {
        return especialidadTipoId;
    }

    public Long getConsultorioId() {
        return consultorioId;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public Canal getCanal() {
        return canal;
    }

    public PerfilPaciente getPerfilPaciente() {
        return perfilPaciente;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getCausaCancelacion() {
        return causaCancelacion;
    }
}
