package com.destiny.sagaorchestrator.infrastructure.repository;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SagaRepositoryImpl implements SagaRepository {

    private final SagaJpaRepository sagaJpaRepository;

    @Override
    public void createSaga(SagaState saga) {

        sagaJpaRepository.save(saga);
    }

    @Override
    public SagaState findByOrderId(UUID orderId) {

        return sagaJpaRepository.findByOrderId(orderId);
    }

    @Override
    public SagaState findById(UUID uuid) {

        return sagaJpaRepository.findById(uuid).orElse(null);
    }

    @Override
    public List<SagaState> findAll() {

        return sagaJpaRepository.findAll();
    }
}
