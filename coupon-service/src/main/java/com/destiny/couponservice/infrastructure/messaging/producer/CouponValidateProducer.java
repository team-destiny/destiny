package com.destiny.couponservice.infrastructure.messaging.producer;

import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateFailEvent;
import com.destiny.couponservice.infrastructure.messaging.event.result.CouponValidateSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponValidateProducer {

    private static final String SUCCESS_TOPIC = "coupon-use-success";
    private static final String FAIL_TOPIC = "coupon-use-fail";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSuccess(CouponValidateSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(SUCCESS_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[CouponValidateProducer] success: {}", json);
        } catch (Exception e) {
            log.error("Failed to send coupon-use-success: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - coupon-use-success", e);
        }
    }

    public void sendFail(CouponValidateFailEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(FAIL_TOPIC, json).get(5, TimeUnit.SECONDS);

            log.info("[CouponValidateProducer] fail: {}", json);
        } catch (Exception e) {
            log.error("Failed to send coupon-use-fail: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - coupon-use-fail", e);
        }
    }
}
