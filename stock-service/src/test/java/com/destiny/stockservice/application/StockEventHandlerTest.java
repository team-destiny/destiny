package com.destiny.stockservice.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationItem;
import com.destiny.stockservice.application.dto.order.OrderCompletedEvent;
import com.destiny.stockservice.application.dto.stock.StockCreateEvent;
import com.destiny.stockservice.application.service.StockReservationService;
import com.destiny.stockservice.application.service.StockService;
import com.destiny.stockservice.domain.result.ConfirmedStockCancelResult;
import com.destiny.stockservice.domain.result.StockReservationCancelResult;
import com.destiny.stockservice.domain.result.StockReservationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockEventHandlerTest {

    @InjectMocks
    private StockEventHandler stockEventHandler;

    @Mock
    private StockReservationService stockReservationService;

    @Mock
    private StockEventPublisher stockEventPublisher;

    @Mock
    private StockService stockService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("재고 생성 이벤트를 처리한다.")
    void handleStockCreate() throws Exception {
        // given
        StockCreateEvent event = new StockCreateEvent(UUID.randomUUID(), 100);
        String message = objectMapper.writeValueAsString(event);

        // when
        stockEventHandler.handleStockCreate(message);

        // then
        verify(stockService, times(1)).createStock(any(StockCreateEvent.class));
    }

    @Test
    @DisplayName("재고 예약 성공 시 성공 이벤트를 발행한다.")
    void handleStockReservationSuccess() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        StockReservationEvent event = new StockReservationEvent(orderId,
            List.of(new StockReservationItem(UUID.randomUUID(), 1)));
        String message = objectMapper.writeValueAsString(event);

        given(stockReservationService.reserveStock(any())).willReturn(
            StockReservationResult.RESERVED);

        // when
        stockEventHandler.handleStockReservation(message);

        // then
        verify(stockEventPublisher, times(1)).publishReservationSuccess(anyString());
    }

    @Test
    @DisplayName("재고 예약 실패(재고 부족 등) 시 실패 이벤트를 발행한다.")
    void handleStockReservationFail() throws Exception {
        // given
        StockReservationEvent event = new StockReservationEvent(UUID.randomUUID(), List.of());
        String message = objectMapper.writeValueAsString(event);

        given(stockReservationService.reserveStock(any())).willReturn(
            StockReservationResult.OUT_OF_STOCK);

        // when
        stockEventHandler.handleStockReservation(message);

        // then
        verify(stockEventPublisher, times(1)).publishReservationFail(anyString());
    }

    @Test
    @DisplayName("재고 예약 취소 성공 시 성공 이벤트를 발행한다.")
    void handleStockReservationCancelSuccess() throws Exception {
        // given
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        StockReservationCancelEvent event = new StockReservationCancelEvent(
            sagaId,
            orderId,
            List.of(new StockReservationCancelEvent.StockReservationCancelItem(UUID.randomUUID(), 1))
        );
        String message = objectMapper.writeValueAsString(event);

        given(stockReservationService.cancelReservedStock(any())).willReturn(
            StockReservationCancelResult.CANCEL_SUCCEEDED);

        // when
        stockEventHandler.handleStockReservationCancel(message);

        // then
        verify(stockEventPublisher, times(1)).publishReservationCancelSuccess(anyString());
    }

    @Test
    @DisplayName("확정된 재고 취소 성공 시 성공 이벤트를 발행한다.")
    void handleConfirmedStockCancelSuccess() throws Exception {
        // given
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        ConfirmedStockCancelEvent event = new ConfirmedStockCancelEvent(
            sagaId,
            orderId,
            List.of(new ConfirmedStockCancelEvent.StockCancelItem(UUID.randomUUID(), 1))
        );

        String message = objectMapper.writeValueAsString(event);

        given(stockReservationService.cancelConfirmedStock(any())).willReturn(
            ConfirmedStockCancelResult.CANCEL_SUCCEEDED);

        // when
        stockEventHandler.handleConfirmedStockCancel(message);

        // then
        verify(stockEventPublisher, times(1)).publishConfirmedStockCancelSuccess(anyString());
    }

    @Test
    @DisplayName("주문 완료 후 품절된 상품이 있으면 품절 이벤트를 발행한다.")
    void handleOrderCompletedWithSoldOut() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderCompletedEvent event = new OrderCompletedEvent(orderId, null, null, null, null, null,
            null);
        String message = objectMapper.writeValueAsString(event);
        List<UUID> soldOutIds = List.of(UUID.randomUUID());

        given(stockReservationService.commitStock(orderId)).willReturn(soldOutIds);

        // when
        stockEventHandler.handleOrderCompleted(message);

        // then
        verify(stockEventPublisher, times(1)).publishProductSoldOut(anyString());
    }

    @Test
    @DisplayName("주문 완료 후 품절된 상품이 없으면 이벤트를 발행하지 않는다.")
    void handleOrderCompletedNoSoldOut() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderCompletedEvent event = new OrderCompletedEvent(orderId, null, null, null, null, null,
            null);
        String message = objectMapper.writeValueAsString(event);

        given(stockReservationService.commitStock(orderId)).willReturn(List.of());

        // when
        stockEventHandler.handleOrderCompleted(message);

        // then
        verify(stockEventPublisher, never()).publishProductSoldOut(anyString());
    }
}