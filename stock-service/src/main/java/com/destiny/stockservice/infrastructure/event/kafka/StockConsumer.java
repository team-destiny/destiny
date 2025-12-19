package com.destiny.stockservice.infrastructure.event.kafka;

import com.destiny.stockservice.application.StockEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockConsumer {

    private final StockEventHandler stockEventHandler;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 1))
    @KafkaListener(topics = "stock-create-message", groupId = "orchestrator")
    public void consumeStockCreate(String message) {
        stockEventHandler.handleStockCreate(message);
    }

    @RetryableTopic(attempts = "10", backoff = @Backoff(delay = 50, multiplier = 1))
    @KafkaListener(topics = "stock-reservation-request", groupId = "orchestrator")
    public void consumeStockReservation(String message) {
        stockEventHandler.handleStockReservation(message);
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 100, multiplier = 2))
    @KafkaListener(topics = "stock-reservation-cancel", groupId = "orchestrator")
    public void consumeStockReservationCancel(String message) {
        stockEventHandler.handleStockReservationCancel(message);
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 100, multiplier = 2))
    @KafkaListener(topics = "stock-cancel-request", groupId = "orchestrator")
    public void consumeConfirmedStockCancel(String message) {
        stockEventHandler.handleConfirmedStockCancel(message);
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = "order-create-success", groupId = "orchestrator")
    public void consumeOrderCompleted(String message) {
        stockEventHandler.handleOrderCompleted(message);
    }

    @DltHandler
    public void handleStockDlt(
        @Payload String payload,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage
    ) {
        log.error("DLT Topic       : {}", topic);
        log.error("원본 메시지      : {}", payload);
        log.error("예외 메시지      : {}", exceptionMessage);

        if (topic.contains("stock-reservation-request")) {
            log.error("재고 예약 요청 처리 실패");

        } else if (topic.contains("stock-reservation-cancel")) {
            log.error("재고 예약 취소 처리 실패");

        } else if (topic.contains("stock-cancel-request")) {
            log.error("확정 재고 취소 요청 처리 실패");

        } else if (topic.contains("stock-create-message")) {
            log.error("재고 생성 처리 실패");

        } else if (topic.contains("order-create-success")) {
            log.error("주문 완료(재고 커밋) 이벤트 처리 실패");

        } else {
            log.error("알 수 없는 Stock 관련 DLT 메시지");
        }
    }
}
