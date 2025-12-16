package com.destiny.sagaorchestrator.domain.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import java.util.Optional;
import java.util.UUID;

public interface SagaRepository {

    void createSaga(SagaState saga);

    SagaState findByOrderId(UUID uuid);

    SagaState findById(UUID uuid);
}
