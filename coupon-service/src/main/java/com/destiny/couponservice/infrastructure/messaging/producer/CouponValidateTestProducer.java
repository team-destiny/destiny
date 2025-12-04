package com.destiny.couponservice.infrastructure.messaging.producer;

import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponValidateTestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String REQUEST_TOPIC = "coupon-use-request";

    public void sendTest(CouponValidateCommand command) {
        try {
            String json = objectMapper.writeValueAsString(command);
            kafkaTemplate.send(REQUEST_TOPIC, json);
            log.info("[CouponValidateTestProducer] send to {}: {}", REQUEST_TOPIC, json);
        } catch (Exception e) {
            log.error("[CouponValidateTestProducer] send failed: {}", e.getMessage(), e);
        }
    }
}
