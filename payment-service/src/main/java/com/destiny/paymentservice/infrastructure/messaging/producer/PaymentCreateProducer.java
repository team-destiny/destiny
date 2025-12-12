package com.destiny.paymentservice.infrastructure.messaging.producer;

import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentFailEvent;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCreateProducer {

    private static final String CREATE_SUCCESS_TOPIC = "payment-create-success";
    private static final String CREATE_FAIL_TOPIC = "payment-create-fail";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCreateSuccess(PaymentSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(CREATE_SUCCESS_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentProducer] success: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-create-success: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-create-success", e);

        }
    }

    public void sendCreateFail(PaymentFailEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(CREATE_FAIL_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentProducer] fail: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-create-fail: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-create-fail", e);
        }
    }
}
