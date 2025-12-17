package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.ProductReopenEvent;
import com.destiny.stockservice.application.dto.ProductSoldOutEvent;
import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelFailEvent;
import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelSuccessEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelFailEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelSuccessEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationFailEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void publishStockReservationSuccessEvent(
        StockReservationSuccessEvent stockReservationSuccessEvent
    ) {
        String message = objectMapper.writeValueAsString(stockReservationSuccessEvent);
        kafkaTemplate.send("stock-reservation-success", message);
    }

    @SneakyThrows
    public void publishStockReservationFailEvent(
        StockReservationFailEvent stockReservationFailEvent
    ) {
        String message = objectMapper.writeValueAsString(stockReservationFailEvent);
        kafkaTemplate.send("stock-reservation-fail", message);
    }

    @SneakyThrows
    public void publishStockReservationCancelSuccessEvent(
        StockReservationCancelSuccessEvent event
    ) {
        String message = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("stock-reservation-cancel-success", message);
    }

    @SneakyThrows
    public void publishStockReservationCancelFailEvent(
        StockReservationCancelFailEvent event
    ) {
        String message = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("stock-reservation-cancel-fail", message);
    }

    @SneakyThrows
    public void publishConfirmedStockCancelSuccessEvent(
        ConfirmedStockCancelSuccessEvent event
    ) {
        String message = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("stock-cancel-success", message);
    }

    @SneakyThrows
    public void publishConfirmedStockCancelFailEvent(
        ConfirmedStockCancelFailEvent event
    ) {
        String message = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("stock-cancel-fail", message);
    }

    @SneakyThrows
    public void publishProductSoldOutEvent(ProductSoldOutEvent productSoldOutEvent) {
        String event = objectMapper.writeValueAsString(productSoldOutEvent);
        kafkaTemplate.send("product-close-event", event);
    }

    @SneakyThrows
    public void publishProductReopenEvent(ProductReopenEvent productReopenEvent) {
        String event = objectMapper.writeValueAsString(productReopenEvent);
        kafkaTemplate.send("product-reopen-event", event);
    }
}
