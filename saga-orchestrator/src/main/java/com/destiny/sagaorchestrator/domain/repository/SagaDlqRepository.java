package com.destiny.sagaorchestrator.domain.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;

public interface SagaDlqRepository {

    void save(SagaDlqMessage message);
}
