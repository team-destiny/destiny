package com.destiny.sagaorchestrator.infrastructure.repository;

import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SagaRepositoryImpl implements SagaRepository {

    private final SagaJpaRepository sagaJpaRepository;

}
