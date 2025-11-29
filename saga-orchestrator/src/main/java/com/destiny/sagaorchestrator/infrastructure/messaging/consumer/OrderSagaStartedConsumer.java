package com.destiny.sagaorchestrator.infrastructure.messaging.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderSagaStartedConsumer {

    @KafkaListener(topics = "order-saga-started", groupId = "saga-orchestrator")
    public void consumer(String message) {
      log.info("SagaStartedEvent Received : {}", message);
    }

}
