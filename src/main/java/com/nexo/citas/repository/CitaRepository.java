package com.nexo.citas.repository;

import com.nexo.citas.entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<CitaEntity, String> {

    Optional<CitaEntity> findByNumero(String numero);

    List<CitaEntity> findByPacienteIdOrderByFechaDesc(String pacienteId);

    @Query("SELECT c FROM CitaEntity c WHERE c.consultorioId = :consultorioId "
            + "AND c.estado IN ('CREADA','FACTURADA','REAGENDADA_SISTEMA') "
            + "AND c.horaInicio < :fin AND c.horaFin > :inicio")
    List<CitaEntity> findByRango(@Param("consultorioId") Long consultorioId,
                                 @Param("inicio") LocalDateTime inicio,
                                 @Param("fin") LocalDateTime fin);

    /**
     * Citas activas (no CANCELADA/CANCELADA_USUARIO/NO_ASISTIO) de una agenda
     * en un dia determinado. Se usa tanto para el chequeo de ocupacion de un
     * slot puntual al crear una cita como para cruzar disponibilidad en el
     * endpoint /disponibilidad.
     */
    @Query("SELECT c FROM CitaEntity c WHERE c.agendaId = :agendaId AND c.fecha = :fecha "
            + "AND c.estado NOT IN ('CANCELADA','CANCELADA_USUARIO','NO_ASISTIO')")
    List<CitaEntity> findActivasByAgendaAndFecha(@Param("agendaId") Long agendaId,
                                                 @Param("fecha") LocalDateTime fecha);
}
