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
public class PaymentProducer {

    private static final String SUCCESS_TOPIC = "payment-confirm-success";
    private static final String FAIL_TOPIC = "payment-confirm-fail";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSuccess(PaymentSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(SUCCESS_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentValidateProducer] success: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-confirm-success: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-confirm-success", e);

        }
    }

    public void sendFail(PaymentFailEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(FAIL_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentValidateProducer] fail: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-confirm-fail: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-confirm-fail", e);
        }
    }
}
