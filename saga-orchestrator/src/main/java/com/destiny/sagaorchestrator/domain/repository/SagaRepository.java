package com.destiny.sagaorchestrator.domain.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaState;

public interface SagaRepository {

    void createSaga(SagaState saga);
}
