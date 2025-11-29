package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCreateRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;

    @Transactional
    public void createSaga(OrderCreateRequestEvent event) {

        SagaState saga = SagaState.of(
            event.orderId(),
            event.userId()
        );

        // TODO : 상품 검증 이밴트 발행
        event.items().forEach(item -> {

        });

        sagaRepository.createSaga(saga);
    }
}
