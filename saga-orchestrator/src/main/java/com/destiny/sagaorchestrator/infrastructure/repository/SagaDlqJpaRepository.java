package com.destiny.sagaorchestrator.infrastructure.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaDlqJpaRepository extends JpaRepository<SagaDlqMessage, UUID> {

}
