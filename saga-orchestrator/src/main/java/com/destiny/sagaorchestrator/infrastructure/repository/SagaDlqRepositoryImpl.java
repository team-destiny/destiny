package com.destiny.sagaorchestrator.infrastructure.repository;

import com.destiny.sagaorchestrator.domain.repository.SagaDlqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SagaDlqRepositoryImpl implements SagaDlqRepository {

    private final SagaDlqJpaRepository sagaDlqJpaRepository;

}
