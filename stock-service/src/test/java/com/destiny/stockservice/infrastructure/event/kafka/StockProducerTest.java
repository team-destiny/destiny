package com.destiny.stockservice.infrastructure.event.kafka;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class StockProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private StockProducer stockProducer;

    @Test
    @DisplayName("[재고 예약 성공 이벤트 발행] topic : stock-reservation-success")
    void publishReservationSuccess_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-reservation-success\"}";

        stockProducer.publishReservationSuccess(event);

        verify(kafkaTemplate).send("stock-reservation-success", event);
    }

    @Test
    @DisplayName("[재고 예약 실패 이벤트 발행] topic : stock-reservation-fail")
    void publishReservationFail_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-reservation-fail\"}";

        stockProducer.publishReservationFail(event);

        verify(kafkaTemplate).send("stock-reservation-fail", event);
    }

    @Test
    @DisplayName("[재고 예약 취소 성공 이벤트 발행] topic : stock-reservation-cancel-success")
    void publishReservationCancelSuccess_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-reservation-cancel-success\"}";

        stockProducer.publishReservationCancelSuccess(event);

        verify(kafkaTemplate).send("stock-reservation-cancel-success", event);
    }

    @Test
    @DisplayName("[재고 예약 취소 실패 이벤트 발행] topic : stock-reservation-cancel-fail")
    void publishReservationCancelFail_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-reservation-cancel-fail\"}";

        stockProducer.publishReservationCancelFail(event);

        verify(kafkaTemplate).send("stock-reservation-cancel-fail", event);
    }

    @Test
    @DisplayName("[확정 재소 취소 성공 이벤트 발행] topic : stock-cancel-success")
    void publishConfirmedStockCancelSuccess_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-cancel-success\"}";

        stockProducer.publishConfirmedStockCancelSuccess(event);

        verify(kafkaTemplate).send("stock-cancel-success", event);
    }

    @Test
    @DisplayName("[확정 재소 취소 실패 이벤트 발행] topic : stock-cancel-fail")
    void publishConfirmedStockCancelFail_sendsToCorrectTopic() {
        String event = "{\"type\":\"stock-cancel-fail\"}";

        stockProducer.publishConfirmedStockCancelFail(event);

        verify(kafkaTemplate).send("stock-cancel-fail", event);
    }

    @Test
    @DisplayName("[상품 품절 이벤트 발행] topic : product-close-event")
    void publishProductSoldOut_sendsToCorrectTopic() {
        String event = "{\"type\":\"product-close-event\"}";

        stockProducer.publishProductSoldOut(event);

        verify(kafkaTemplate).send("product-close-event", event);
    }

    @Test
    @DisplayName("[상품 재판매 이벤트 발행] topic : product-reopen-event")
    void publishProductReopen_sendsToCorrectTopic() {
        String event = "{\"type\":\"product-reopen-event\"}";

        stockProducer.publishProductReopen(event);

        verify(kafkaTemplate).send("product-reopen-event", event);
    }
}