package com.nexo.citas.repository;

import com.nexo.citas.entity.DisponibilidadSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DisponibilidadSlotRepository extends JpaRepository<DisponibilidadSlotEntity, Long> {

    boolean existsByConsultorioIdAndFechaAndHoraInicio(Long consultorioId, LocalDate fecha, LocalTime horaInicio);

    List<DisponibilidadSlotEntity> findByConsultorioIdAndFechaAndReservadaPorIsNull(
            Long consultorioId, LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM DisponibilidadSlotEntity s WHERE s.id = :id")
    java.util.Optional<DisponibilidadSlotEntity> findByIdForUpdate(@Param("id") Long id);
}
