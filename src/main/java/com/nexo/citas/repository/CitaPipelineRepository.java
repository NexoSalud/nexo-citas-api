package com.nexo.citas.repository;

import com.nexo.citas.entity.CitaPipelineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaPipelineRepository extends JpaRepository<CitaPipelineEntity, Long> {

    List<CitaPipelineEntity> findByCitaOrigenIdOrderByOrdenAsc(String citaOrigenId);
}
