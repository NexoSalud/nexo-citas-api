package com.nexo.citas.repository;

import com.nexo.citas.entity.CitaEstadoHistorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaEstadoHistorialRepository extends JpaRepository<CitaEstadoHistorialEntity, Long> {

    List<CitaEstadoHistorialEntity> findByCitaIdOrderByTimestampAsc(String citaId);
}
