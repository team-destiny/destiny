package com.destiny.sagaorchestrator.domain.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import java.util.List;

public interface SagaDlqRepository {

    void save(SagaDlqMessage message);

    List<SagaDlqMessage> findAll();

}
