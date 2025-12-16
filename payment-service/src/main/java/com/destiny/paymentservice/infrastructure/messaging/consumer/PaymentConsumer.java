package com.destiny.paymentservice.infrastructure.messaging.consumer;

import com.destiny.paymentservice.application.service.impl.PaymentServiceImpl;
import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCancelCommand;
import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentServiceImpl paymentService;

    @KafkaListener(topics = "payment-create-request", groupId = "payment-service")
    public void onPayment(String message) {
        try {
            log.info("[PaymentConsumer] Received: {}", message);
            PaymentCommand command = objectMapper.readValue(message, PaymentCommand.class);
            paymentService.requestPayment(command);
        } catch (JsonProcessingException e) {
            log.error("[PaymentConsumer] JSON 파싱 오류 - 메시지 무시: {}", message, e);
        }
    }

    @KafkaListener(topics = "payment-cancel-request", groupId = "payment-service")
    public void cancelPayment(String message) {
        try {
            log.info("[PaymentConsumer] Received: {}", message);
            PaymentCancelCommand command = objectMapper.readValue(message, PaymentCancelCommand.class);
            paymentService.cancelPayment(command);
        } catch (JsonProcessingException e) {
            log.error("[PaymentConsumer] JSON 파싱 오류 - 메시지 무시: {}", message, e);
        }
    }
}
