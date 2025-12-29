package com.destiny.stockservice.infrastructure.event.kafka;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
    @DisplayName("재고 예약 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
    void consumeStockReservation_callsHandler() {
        String message = "{\"type\":\"stock-reservation-request\"}";

        stockConsumer.consumeStockReservation(message);

        verify(stockEventHandler).handleStockReservation(message);
    }

    @Test
    @DisplayName("재고 예약 취소 메세지 수신시 재고 이벤트 핸들러로 전달 테스트")
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
    @DisplayName("Stock DLT 처리: 알려진 토픽이면 예외 없이 로깅한다")
    void handleStockDlt_doesNotThrow_forKnownTopic() {
        assertDoesNotThrow(() -> stockConsumer.handleStockDlt(
            "{\"payload\":\"x\"}",
            "stock-reservation-request-dlt",
            "some exception"
        ));

        verifyNoInteractions(stockEventHandler);
    }

    @Test
    @DisplayName("Stock DLT 처리: 알 수 없는 토픽이어도 예외 없이 로깅한다")
    void handleStockDlt_doesNotThrow_forUnknownTopic() {
        assertDoesNotThrow(() -> stockConsumer.handleStockDlt(
            "{\"payload\":\"x\"}",
            "some-unknown-topic-dlt",
            "some exception"
        ));

        verifyNoInteractions(stockEventHandler);
    }
}