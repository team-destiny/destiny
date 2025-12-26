package com.destiny.stockservice.infrastructure.event.kafka;

import static org.mockito.Mockito.verify;

import com.destiny.stockservice.application.StockEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockConsumerTest {

    @Mock
    private StockEventHandler stockEventHandler;

    @InjectMocks
    private StockConsumer stockConsumer;

    @Test
    @DisplayName("재고 생성 메세지 수신시 재고 이벤트 핸들러로 전달")
    void consumeStockCreate_callsHandler() {
        String message = "{\"type\":\"stock-create\"}";

        stockConsumer.consumeStockCreate(message);

        verify(stockEventHandler).handleStockCreate(message);
    }

    @Test
    @DisplayName("재고 생성 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
    void consumeStockReservation_callsHandler() {
        String message = "{\"type\":\"stock-reservation-request\"}";

        stockConsumer.consumeStockReservation(message);

        verify(stockEventHandler).handleStockReservation(message);
    }

    @Test
    @DisplayName("재고 예약 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
    void consumeStockReservationCancel_callsHandler() {
        String message = "{\"type\":\"stock-reservation-cancel\"}";

        stockConsumer.consumeStockReservationCancel(message);

        verify(stockEventHandler).handleStockReservationCancel(message);
    }

    @Test
    @DisplayName("확정 재고 취소 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
    void consumeConfirmedStockCancel_callsHandler() {
        String message = "{\"type\":\"stock-cancel-request\"}";

        stockConsumer.consumeConfirmedStockCancel(message);

        verify(stockEventHandler).handleConfirmedStockCancel(message);
    }

    @Test
    @DisplayName("주문 완료 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
    void consumeOrderCompleted_callsHandler() {
        String message = "{\"type\":\"order-create-success\"}";

        stockConsumer.consumeOrderCompleted(message);

        verify(stockEventHandler).handleOrderCompleted(message);
    }

    @Test
    @DisplayName("DitHandler가 알고 있는 토픽에 대해 예외를 발생하지 않는 테스트")
    void handleStockDlt_doesNotThrow_forKnownTopic() {
        stockConsumer.handleStockDlt(
            "{\"payload\":\"x\"}",
            "stock-reservation-request-dlt",
            "some exception"
        );
        // 예외 없이 동작하면 통과 (현재 메서드가 로깅만 수행)
    }

    @Test
    @DisplayName("DitHandler가 모르는 토픽에 예외를 발생하는 테스트")
    void handleStockDlt_doesNotThrow_forUnknownTopic() {
        stockConsumer.handleStockDlt(
            "{\"payload\":\"x\"}",
            "some-unknown-topic-dlt",
            "some exception"
        );
        // 예외 없이 동작하면 통과
    }
}