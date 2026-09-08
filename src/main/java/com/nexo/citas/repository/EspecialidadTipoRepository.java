package com.nexo.citas.repository;

import com.nexo.citas.entity.EspecialidadTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecialidadTipoRepository extends JpaRepository<EspecialidadTipoEntity, Long> {

    List<EspecialidadTipoEntity> findByHabilitadaTrueOrderByNombreAsc();
}
