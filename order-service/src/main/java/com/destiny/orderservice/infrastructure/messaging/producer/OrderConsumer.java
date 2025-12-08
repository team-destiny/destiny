package com.destiny.orderservice.infrastructure.messaging.producer;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCreateFailedEvent;
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

    @KafkaListener(topics = "order-create-failed", groupId = "saga-orchestrator")
    public void createOrderFailed(String message) {

        try {
            log.info("Join Order Service : order-create-failed");
            OrderCreateFailedEvent event = objectMapper.readValue(
                message, OrderCreateFailedEvent.class);

            orderService.failOrder(event);
        } catch (JsonProcessingException e) {

            log.error("Order Service : order-create-failed json processing error", e);
        }
    }

}
