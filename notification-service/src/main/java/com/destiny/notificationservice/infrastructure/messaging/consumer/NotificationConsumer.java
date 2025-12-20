package com.destiny.notificationservice.infrastructure.messaging.consumer;

import com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
import com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent;
import com.destiny.notificationservice.application.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${kafka.topics.saga-failure}")
    public void handleSagaCreateFail(String message) throws Exception {

        SagaCreateFailedEvent event = objectMapper.readValue(message, SagaCreateFailedEvent.class);
        log.info("[Kafka] 사가 실패 이벤트 수신: {}", event);

        notificationService.sendSagaCreateFailedNotification(event);

    }


    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${kafka.topics.order-success}")
    public void handleOrderCreateSuccess(String message) throws Exception {

        OrderCreateSuccessEvent event = objectMapper.readValue(message,
            OrderCreateSuccessEvent.class);
        log.info("[Kafka] 주문 성공 이벤트 수신: {}", event);

        notificationService.sendOrderCreateSuccessNotification(event);

    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${kafka.topics.order-cancel-requested}")
    public void handleOrderCancelRequested(String message) throws Exception {

        OrderCancelRequestedEvent event = objectMapper.readValue(message,
            OrderCancelRequestedEvent.class);
        log.info("[Kafka] 주문 취소 요청 이벤트 수신: {}", event);

        notificationService.sendOrderCancelRequestedNotification(event);

    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${kafka.topics.order-cancel-failed}")
    public void handleOrderCancelFailed(String message) throws Exception {

        OrderCancelFailedEvent event = objectMapper.readValue(message,
            OrderCancelFailedEvent.class);
        log.info("[Kafka] 주문 취소 실패 이벤트 수신: {}", event);

        notificationService.sendOrderCancelFailedNotification(event);

    }

    @KafkaListener(topics = "${kafka.topics.dlq-message}")
    public void handleDlpMessage(String message) throws Exception {

        NotificationDlqMessageEvent event = objectMapper.readValue(message,
            NotificationDlqMessageEvent.class);
        log.warn("[Kafka][DLQ] DLQ 메시지 수신: originalTopic={}, dlqTopic={}, retryCount={}",
            event.originalTopic(), event.dlqTopic(), event.retryCount());

        notificationService.sendDlqNotification(event);
    }
}
