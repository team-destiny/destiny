package com.destiny.couponservice.infrastructure.messaging.producer;


import com.destiny.couponservice.infrastructure.messaging.event.command.CouponCancelFailEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponCancelSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponCancelProducer {

    private static final String SUCCESS_TOPIC = "coupon-cancel-success";
    private static final String FAIL_TOPIC = "coupon-cancel-fail";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSuccess(CouponCancelSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(SUCCESS_TOPIC, json).get(5, TimeUnit.SECONDS);
            log.info("[CouponCancelProducer] success: {}", json);
        } catch (Exception e) {
            log.error("Failed to send coupon-cancel-success: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - coupon-cancel-success", e);
        }
    }

    public void sendFail(CouponCancelFailEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(FAIL_TOPIC, json).get(5, TimeUnit.SECONDS);
            log.info("[CouponCancelProducer] fail: {}", json);
        } catch (Exception e) {
            log.error("Failed to send coupon-cancel-fail: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka 전송 실패 - coupon-cancel-fail", e);
        }
    }
}
