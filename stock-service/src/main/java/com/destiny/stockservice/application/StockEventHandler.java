package com.destiny.stockservice.application;

import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelFailEvent;
import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelSuccessEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelFailEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelSuccessEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationFailEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationSuccessEvent;
import com.destiny.stockservice.application.dto.order.OrderCompletedEvent;
import com.destiny.stockservice.application.dto.product.ProductSoldOutEvent;
import com.destiny.stockservice.application.dto.stock.StockCreateEvent;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockEventHandler {

    private final StockReservationService stockReservationService;

    private final StockEventPublisher stockEventPublisher;

    private final StockService stockService;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void handleStockCreate(String message) {

        StockCreateEvent event = objectMapper
            .readValue(message, StockCreateEvent.class);

        stockService.createStock(event);
    }

    @SneakyThrows
    public void handleStockReservation(String message) {
        StockReservationEvent event = objectMapper
            .readValue(message, StockReservationEvent.class);

        StockReservationResult result = stockReservationService.reserveStock(event);

        switch(result) {
            case RESERVED -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new StockReservationSuccessEvent(
                        event.orderId(),
                        event.items()
                    )
                );

                stockEventPublisher.publishReservationSuccess(resultMessage);

                log.info("[STOCK RESERVATION SUCCESS] {}", result.getDescription());
            }

            case INVALID_REQUEST, OUT_OF_STOCK -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new StockReservationFailEvent(
                        event.orderId()
                    )
                );

                stockEventPublisher.publishReservationFail(resultMessage);

                log.warn("[STOCK RESERVATION FAIL] {}", result.getDescription());
            }
        }
    }

    @SneakyThrows
    public void handleStockReservationCancel(String message) {
        StockReservationCancelEvent event = objectMapper
            .readValue(message, StockReservationCancelEvent.class);

        StockReservationCancelResult result = stockReservationService.cancelReservedStock(event);

        switch(result) {
            case CANCEL_SUCCEEDED -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new StockReservationCancelSuccessEvent(
                        event.sagaId()
                    )
                );

                stockEventPublisher.publishReservationCancelSuccess(resultMessage);

                log.info("[STOCK RESERVATION CANCEL SUCCEEDED] {}", result.getDescription());
            }

            case NO_RESERVATION, ALREADY_CANCELED -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new StockReservationCancelFailEvent(
                        event.sagaId(),
                        result.getDescription()
                    )
                );

                stockEventPublisher.publishReservationCancelFail(resultMessage);

                log.warn("[STOCK RESERVATION CANCEL FAIL] {}", result.getDescription());
            }
        }
    }

    @SneakyThrows
    public void handleConfirmedStockCancel(String message) {
        ConfirmedStockCancelEvent event = objectMapper
            .readValue(message, ConfirmedStockCancelEvent.class);

        ConfirmedStockCancelResult result = stockReservationService.cancelConfirmedStock(event);

        switch(result) {
            case CANCEL_SUCCEEDED, NO_RESERVATION -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new ConfirmedStockCancelSuccessEvent(
                        event.sagaId()
                    )
                );

                stockEventPublisher.publishConfirmedStockCancelSuccess(resultMessage);

                log.info("CONFIRMED STOCK CANCEL SUCCEEDED {}", result.getDescription());
            }

            case  CANCEL_FAILED, INVALID_REQUEST -> {
                String resultMessage = objectMapper.writeValueAsString(
                    new ConfirmedStockCancelFailEvent(
                        event.sagaId(),
                        result.getDescription()
                    )
                );

                stockEventPublisher.publishConfirmedStockCancelFail(resultMessage);

                log.warn("CONFIRMED STOCK CANCEL FAILED {}", result.getDescription());
            }
        }
    }

    @SneakyThrows
    public void handleOrderCompleted(String message) {
        OrderCompletedEvent event = objectMapper
            .readValue(message, OrderCompletedEvent.class);

        List<UUID> soldOutProductIds = stockReservationService.commitStock(event.orderId());

        if (soldOutProductIds.isEmpty()) {
            return;
        }

        String resultMessage = objectMapper.writeValueAsString(
            new ProductSoldOutEvent(soldOutProductIds)
        );

        stockEventPublisher.publishProductSoldOut(resultMessage);

        log.info("[PRODUCT SOLD OUT MESSAGE PUBLISHED] {}", soldOutProductIds);
    }
}
