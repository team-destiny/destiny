package com.destiny.couponservice.infrastructure.messaging.consumer;

import com.destiny.couponservice.application.service.IssuedCouponService;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponRollbackRequestEvent;
import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final ObjectMapper objectMapper;
    private final IssuedCouponService issuedCouponService;

    @KafkaListener(topics = "coupon-use-request", groupId = "coupon-service")
    public void onCouponValidate(String message) {
        CouponValidateCommand command;
        try {
            log.info("[CouponValidateConsumer] Received: {}", message);
            command = objectMapper.readValue(message, CouponValidateCommand.class);
        } catch (Exception e) {
            log.error("[CouponValidateConsumer] JSON 파싱 오류 - 메시지 무시: {}", message, e);
            return;
        }

        issuedCouponService.handleCouponValidate(command);
    }

    @KafkaListener(topics = "coupon-use-rollback", groupId = "coupon-service")
    public void onCouponRollback(String message) {
        CouponRollbackRequestEvent event;
        try {
            log.info("[CouponRollbackRequestEvent] Received: {}", message);
            event = objectMapper.readValue(message, CouponRollbackRequestEvent.class);
        } catch (Exception e) {
            log.error("[CouponRollbackRequestEvent] JSON 파싱 오류 - 메시지 무시: {}", message, e);
            return;
        }

        issuedCouponService.couponRollback(event);
    }

}
