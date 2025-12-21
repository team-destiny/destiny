package com.destiny.notificationservice.infrastructure.messaging.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
import com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent;
import com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent;
import com.destiny.notificationservice.application.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; // ✅ 추가됨
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationService notificationService;

    private ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private NotificationConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new NotificationConsumer(notificationService, objectMapper);
    }

    @Test
    @DisplayName("주문 성공 메시지 수신 시 서비스 호출 확인")
    void handleOrderCreateSuccess() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderCreateSuccessEvent event = new OrderCreateSuccessEvent(
            orderId, UUID.randomUUID(), null, 0, 1000, List.of(), 1000
        );
        String jsonMessage = objectMapper.writeValueAsString(event);

        // when
        consumer.handleOrderCreateSuccess(jsonMessage);

        // then
        ArgumentCaptor<OrderCreateSuccessEvent> captor = ArgumentCaptor.forClass(
            OrderCreateSuccessEvent.class);
        verify(notificationService, times(1)).sendOrderCreateSuccessNotification(captor.capture());

        assertEquals(orderId, captor.getValue().orderId());
    }

    @Test
    @DisplayName("주문 취소 요청 메시지 수신 시 서비스 호출 확인")
    void handleOrderCancelRequested() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderCancelRequestedEvent event = new OrderCancelRequestedEvent(
            orderId, UUID.randomUUID(), 1000, "취소사유"
        );
        String jsonMessage = objectMapper.writeValueAsString(event);

        // when
        consumer.handleOrderCancelRequested(jsonMessage);

        // then
        ArgumentCaptor<OrderCancelRequestedEvent> captor = ArgumentCaptor.forClass(
            OrderCancelRequestedEvent.class);
        verify(notificationService, times(1)).sendOrderCancelRequestedNotification(
            captor.capture());

        assertEquals(orderId, captor.getValue().orderId());
    }

    @Test
    @DisplayName("주문 취소 실패 메시지 수신 시 서비스 호출 확인")
    void handleOrderCancelFailed() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderCancelFailedEvent event = new OrderCancelFailedEvent(
            orderId, UUID.randomUUID(), "PAYMENT_ERROR", "잔액 부족"
        );
        String jsonMessage = objectMapper.writeValueAsString(event);

        // when
        consumer.handleOrderCancelFailed(jsonMessage);

        // then
        ArgumentCaptor<OrderCancelFailedEvent> captor = ArgumentCaptor.forClass(
            OrderCancelFailedEvent.class);
        verify(notificationService, times(1)).sendOrderCancelFailedNotification(captor.capture());

        assertEquals(orderId, captor.getValue().orderId());
    }

    @Test
    @DisplayName("사가 실패 메시지 수신 시 서비스 호출 확인")
    void handleSagaCreateFail() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        SagaCreateFailedEvent event = new SagaCreateFailedEvent(
            orderId, "Step1", "ServiceA", "ERR_001", "Timeout", "Detail"
        );
        String jsonMessage = objectMapper.writeValueAsString(event);

        // when
        consumer.handleSagaCreateFail(jsonMessage);

        // then
        ArgumentCaptor<SagaCreateFailedEvent> captor = ArgumentCaptor.forClass(
            SagaCreateFailedEvent.class);
        verify(notificationService, times(1)).sendSagaCreateFailedNotification(captor.capture());

        assertEquals(orderId, captor.getValue().orderId());
    }

    @Test
    @DisplayName("DLQ 메시지 수신 시 서비스 호출 확인")
    void handleDlpMessage() throws Exception {
        // given
        NotificationDlqMessageEvent event = new NotificationDlqMessageEvent(
            "original-topic", "dlq-topic", 0, 1L, "group-id", "key", "payload", "DeserializeError",
            "Stacktrace...", 1, LocalDateTime.now()
        );
        String jsonMessage = objectMapper.writeValueAsString(event);

        // when
        consumer.handleDlqMessage(jsonMessage);

        // then
        ArgumentCaptor<NotificationDlqMessageEvent> captor = ArgumentCaptor.forClass(
            NotificationDlqMessageEvent.class);
        verify(notificationService, times(1)).sendDlqNotification(captor.capture());

        assertEquals("original-topic", captor.getValue().originalTopic());
    }
}