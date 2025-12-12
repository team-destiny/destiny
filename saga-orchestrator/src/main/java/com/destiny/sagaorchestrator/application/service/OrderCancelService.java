package com.destiny.sagaorchestrator.application.service;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.repository.SagaRepository;
import com.destiny.sagaorchestrator.infrastructure.messaging.event.request.OrderCancelRequestEvent;
import com.destiny.sagaorchestrator.infrastructure.messaging.producer.SagaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelService {

    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;

    @Transactional
    public void cancelOrder(OrderCancelRequestEvent event) {
        SagaState saga = sagaRepository.findByOrderId(event.orderId());


    }
}
