package com.destiny.orderservice.infrastructure.messaging.producer;

import com.destiny.orderservice.infrastructure.messaging.event.outbound.OrderCancelRequestEvent;
import com.destiny.orderservice.infrastructure.messaging.event.outbound.OrderCreateRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderCreate(OrderCreateRequestEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-create-request", payload);
            log.info("[🍏 ORDER-SERVICE -> SAGA-SERVICE SUCCESS] - ORDER CREATE : {}", payload);

        } catch (JsonProcessingException e) {
            log.error("[❌ ORDER-SERVICE -> SAGA-SERVICE FAIL JSON EXCEPTION] - ORDER CREATE : {}", e.getMessage());
        }
    }

    public void sendOrderCancel(OrderCancelRequestEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-cancel-request", message);
            log.info("🍎 ORDER-SERVICE -> SAGA-SERVICE SUCCESS] - ORDER CANCEL : {}", message);

        } catch (JsonProcessingException e) {

            log.info("❌ ORDER-SERVICE -> SAGA-SERVICE FAIL JSON EXCEPTION] - ORDER CANCEL : {}", e.getMessage());
        }
    }
}
