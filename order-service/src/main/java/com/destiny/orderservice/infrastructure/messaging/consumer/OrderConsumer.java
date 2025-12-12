package com.destiny.orderservice.infrastructure.messaging.consumer;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCreateFailedEvent;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCreateSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "order-create-success", groupId = "saga-orchestrator")
    public void createOrderSuccess(String message) {

        try {
            log.info("[🍏 ORDER CREATE SUCCESS] : {}", message);
            OrderCreateSuccessEvent event = objectMapper.readValue(
                message, OrderCreateSuccessEvent.class);

            orderService.successOrder(event);
        } catch (JsonProcessingException e) {

            log.info("[🍏 ORDER CREATE SUCCESS] JSON EXCEPTION : {}", e.getMessage());
        }

    }

    @KafkaListener(topics = "order-create-failed", groupId = "saga-orchestrator")
    public void createOrderFailed(String message) {

        try {
            log.info("[🍏 ORDER CREATE FAILED] : {}", message);
            OrderCreateFailedEvent event = objectMapper.readValue(
                message, OrderCreateFailedEvent.class);

            orderService.failOrder(event);
        } catch (JsonProcessingException e) {

            log.info("[🍏 ORDER CREATE FAILED] JSON EXCEPTION : {}", e.getMessage());
        }
    }


}
