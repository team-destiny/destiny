package com.destiny.orderservice.infrastructure.messaging.producer;

import com.destiny.orderservice.infrastructure.messaging.dto.SagaStartedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(SagaStartedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("order-saga-started", payload);
            log.info("Producer : order-saga-started success -> {}", payload);

        } catch (JsonProcessingException e) {
            log.error("Saga Started Event Fail ", e);
        }
    }
}
