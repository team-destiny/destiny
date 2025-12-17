package com.destiny.stockservice.application.service.message;

import com.destiny.stockservice.application.dto.OrderCompletedEvent;
import com.destiny.stockservice.application.dto.ProductSoldOutEvent;
import com.destiny.stockservice.application.dto.StockCreateEvent;
import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelFailEvent;
import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelSuccessEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelFailEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelSuccessEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationFailEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationSuccessEvent;
import com.destiny.stockservice.application.service.StockReservationService;
import com.destiny.stockservice.application.service.StockService;
import com.destiny.stockservice.domain.result.ConfirmedStockCancelResult;
import com.destiny.stockservice.domain.result.StockReservationCancelResult;
import com.destiny.stockservice.domain.result.StockReservationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {

    private final StockReservationService stockReservationService;

    private final StockProducerService stockProducerService;

    private final StockService stockService;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "stock-create-message")
    public void consumeStockCreateMessage(String stockCreateMessage) {

        StockCreateEvent message = objectMapper
            .readValue(stockCreateMessage, StockCreateEvent.class);

        stockService.createStock(message);

        log.info("상품 재고 정보를 생성했습니다. productId={}", message.productId());
    }

    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "stock-reservation-request")
    public void consumeStockReserveMessage(String stockReservationMessage) {

        StockReservationEvent event = objectMapper
            .readValue(stockReservationMessage, StockReservationEvent.class);

        StockReservationResult result = stockReservationService.reserveStock(event);

        switch(result) {
            case RESERVED -> {
                stockProducerService.publishStockReservationSuccessEvent(
                    new StockReservationSuccessEvent(event.orderId(), event.items())
                );
            }

            case INVALID_REQUEST, OUT_OF_STOCK -> {
                stockProducerService.publishStockReservationFailEvent(
                    new StockReservationFailEvent(event.orderId())
                );
            }
        }
    }

    @SneakyThrows
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(
        groupId = "orchestrator",
        topics = {
            "stock-reservation-cancel",
        }
    )
    public void consumeStockReservationCancelEvent(String stockReservationCancel) {

        StockReservationCancelEvent event = objectMapper
            .readValue(stockReservationCancel, StockReservationCancelEvent.class);

        StockReservationCancelResult result = stockReservationService.cancelReservedStock(event);

        switch(result) {
            case CANCEL_SUCCEEDED -> {
                stockProducerService.publishStockReservationCancelSuccessEvent(
                    new StockReservationCancelSuccessEvent(event.sagaId())
                );
            }

            case NO_RESERVATION, ALREADY_CANCELED -> {
                stockProducerService.publishStockReservationCancelFailEvent(
                    new StockReservationCancelFailEvent(event.sagaId(), result.getDescription())
                );
            }
        }
    }

    @SneakyThrows
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(groupId = "orchestrator", topics = "stock-cancel-request")
    public void consumeConfirmedStockCancelAfterOrder(String stockCancelCommand) {

        ConfirmedStockCancelEvent event = objectMapper
            .readValue(stockCancelCommand, ConfirmedStockCancelEvent.class);

        ConfirmedStockCancelResult result = stockReservationService.cancelConfirmedStock(event);

        switch(result) {
            case CANCEL_SUCCEEDED, NO_RESERVATION -> {
                stockProducerService.publishConfirmedStockCancelSuccessEvent(
                    new ConfirmedStockCancelSuccessEvent(event.sagaId())
                );
            }

            case  CANCEL_FAILED, INVALID_REQUEST -> {
                stockProducerService.publishConfirmedStockCancelFailEvent(
                    new ConfirmedStockCancelFailEvent(event.sagaId(), result.getDescription())
                );
            }
        }
    }


    @SneakyThrows
    @KafkaListener(groupId = "orchestrator", topics = "order-create-success")
    public void consumeOrderCompletedEvent(String orderCompletedEvent) {

        OrderCompletedEvent event = objectMapper
            .readValue(orderCompletedEvent, OrderCompletedEvent.class);


        List<UUID> soldOutProductIds = stockReservationService.commitStock(event.orderId());

        if (soldOutProductIds.isEmpty()) {
            return;
        }

        stockProducerService.publishProductSoldOutEvent(
            new ProductSoldOutEvent(soldOutProductIds)
        );
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
        } else if (topic.contains("stock-reservation-cancel-request")) {
            log.error("재고 예약 취소 처리 실패");
        } else if (topic.contains("stock-create-message")) {
            log.error("재고 생성 처리 실패");
        } else {
            log.error("알 수 없는 Stock 관련 DLT 메시지");
        }
    }

}
