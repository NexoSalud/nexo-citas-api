package com.nexo.citas.repository;

import com.nexo.citas.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEventEntity, Long> {

    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(String status);

    List<OutboxEventEntity> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(String status, LocalDateTime before);
}
