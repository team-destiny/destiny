package com.destiny.paymentservice.infrastructure.messaging.producer;

import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentCancelFailEvent;
import com.destiny.paymentservice.infrastructure.messaging.event.result.PaymentCancelSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancelProducer {

    private static final String CREATE_SUCCESS_TOPIC = "payment-cancel-success";
    private static final String CREATE_FAIL_TOPIC = "payment-cancel-fail";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCancelSuccess(PaymentCancelSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(CREATE_SUCCESS_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentProducer] success: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-cancel-success: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-cancel-success", e);

        }
    }

    public void sendCancelFail(PaymentCancelFailEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(CREATE_FAIL_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[PaymentProducer] fail: {}", json);
        } catch (Exception e) {
            log.error("Failed to send payment-cancel-fail: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - payment-cancel-fail", e);
        }
    }
}
