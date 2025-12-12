package com.destiny.sagaorchestrator.infrastructure.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaJpaRepository extends JpaRepository<SagaState, UUID> {

    SagaState findByOrderId(UUID orderId);
}
