package com.destiny.notificationservice.infrastructure.messaging.consumer;

import com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
import com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent;
import com.destiny.notificationservice.application.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.saga-failure}")
    public void handleSagaCreateFail(String message) {

        try {
            SagaCreateFailedEvent event = objectMapper.readValue(message,
                SagaCreateFailedEvent.class);
            log.info("[Kafka] 사가 실패 이벤트 수신: {}", event);
            notificationService.sendSagaCreateFailedNotification(event);

        } catch (Exception e) {
            log.error("[Kafka] 사가 실패 이벤트 수신 중 에러", e);
        }
    }


    @KafkaListener(topics = "${kafka.topics.order-success}")
    public void handleOrderCreateSuccess(String message) {

        try {
            OrderCreateSuccessEvent event = objectMapper.readValue(message,
                OrderCreateSuccessEvent.class);
            log.info("[Kafka] 주문 성공 이벤트 수신: {}", event);
            notificationService.sendOrderCreateSuccessNotification(event);

        } catch (Exception e) {
            log.error("[Kafka] 주문 성공 이벤트 수신 중 에러", e);
        }

    }

    @KafkaListener(topics = "${kafka.topics.order-cancel-requested}")
    public void handleOrderCancelRequested(String message) {

        try {
            OrderCancelRequestedEvent event = objectMapper.readValue(message,
                OrderCancelRequestedEvent.class);

            log.info("[Kafka] 주문 취소 요청 이벤트 수신: {}", event);

            notificationService.sendOrderCancelRequestedNotification(event);

        } catch (Exception e) {
            log.error("[Kafka] 주문 취소 요청 이벤트 수신 중 에러", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.order-cancel-failed}")
    public void handleOrderCancelFailed(String message) {

        try {
            OrderCancelFailedEvent event = objectMapper.readValue(message,
                OrderCancelFailedEvent.class);

            log.info("[Kafka] 주문 취소 실패 이벤트 수신: {}", event);

            notificationService.sendOrderCancelFailedNotification(event);

        } catch (Exception e) {
            log.error("[Kafka] 주문 취소 실패 이벤트 수신 중 에러", e);
        }
    }
}
