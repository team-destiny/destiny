package com.destiny.orderservice.infrastructure.messaging.consumer;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCancelFailedEvent;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCancelSuccessEvent;
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
            log.info("[⭐️ JOIN ORDER SUCCESS] : ORDER CREATE SUCCESS {}", message);

            OrderCreateSuccessEvent event = objectMapper.readValue(
                message, OrderCreateSuccessEvent.class);
            orderService.createOrderSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("🔥 JOIN ORDER FAIL JSON EXCEPTION] ORDER CREATE SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-create-failed", groupId = "saga-orchestrator")
    public void createOrderFailed(String message) {

        try {
            log.info("[❌ JOIN ORDER SUCCESS] ORDER CREATE FAILED : {}", message);

            OrderCreateFailedEvent event = objectMapper.readValue(
                message, OrderCreateFailedEvent.class);
            orderService.createOrderFailed(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥 JOIN ORDER FAIL JSON EXCEPTION] ORDER CREATE FAILED : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-cancel-success", groupId = "saga-orchestrator")
    public void cancelOrderSuccess(String message) {

        try {
            log.info("[⭐️ JOIN ORDER SUCCESS] ORDER CANCEL SUCCESS : {}", message);

            OrderCancelSuccessEvent event = objectMapper.readValue(
                message, OrderCancelSuccessEvent.class);
            orderService.cancelOrderSuccess(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥 JOIN ORDER FAIL JSON EXCEPTION] ORDER CANCEL SUCCESS : {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-cancel-fail", groupId = "saga-orchestrator")
    public void cancelOrderFailed(String message) {

        try {
            log.info("[❌ JOIN ORDER SUCCESS] ORDER CANCEL FAILED : {}", message);

            OrderCancelFailedEvent event = objectMapper.readValue(
                message, OrderCancelFailedEvent.class);
            orderService.cancelOrderFailed(event);

        } catch (JsonProcessingException e) {

            log.info("[🔥 JOIN ORDER FAIL JSON EXCEPTION] ORDER CANCEL FAILED : {}", e.getMessage());
        }
    }
}
