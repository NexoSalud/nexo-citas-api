package com.nexo.citas.repository;

import com.nexo.citas.entity.Regla3280Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Regla3280Repository extends JpaRepository<Regla3280Entity, Long> {

    List<Regla3280Entity> findByHabilitadaTrueOrderByPrioridadAsc();

    List<Regla3280Entity> findByHabilitadaTrueAndEspecialidadOrigenIdOrderByPrioridadAsc(Long especialidadOrigenId);
}
