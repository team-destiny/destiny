package com.destiny.stockservice.infrastructure.event.kafka;

import com.destiny.stockservice.application.StockEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockProducer implements StockEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publishReservationSuccess(String event) {
        kafkaTemplate.send("stock-reservation-success", event);
    }

    @Override
    public void publishReservationFail(String event) {
        kafkaTemplate.send("stock-reservation-fail", event);
    }

    @Override
    public void publishReservationCancelSuccess(String event) {
        kafkaTemplate.send("stock-reservation-cancel-success", event);
    }

    @Override
    public void publishReservationCancelFail(String event) {
        kafkaTemplate.send("stock-reservation-cancel-fail", event);
    }

    @Override
    public void publishConfirmedStockCancelSuccess(String event) {
        kafkaTemplate.send("stock-cancel-success", event);
    }

    @Override
    public void publishConfirmedStockCancelFail(String event) {
        kafkaTemplate.send("stock-cancel-fail", event);
    }

    @Override
    public void publishProductSoldOut(String event) {
        kafkaTemplate.send("product-close-event", event);
    }

    @Override
    public void publishProductReopen(String event) {
        kafkaTemplate.send("product-reopen-event", event);
    }
}
