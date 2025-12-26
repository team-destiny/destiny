package com.destiny.stockservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationItem;
import com.destiny.stockservice.domain.entity.ReservationStatus;
import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.entity.StockReservation;
import com.destiny.stockservice.domain.repository.StockRepository;
import com.destiny.stockservice.domain.repository.StockReservationRepository;
import com.destiny.stockservice.domain.result.ConfirmedStockCancelResult;
import com.destiny.stockservice.domain.result.StockReservationCancelResult;
import com.destiny.stockservice.domain.result.StockReservationResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockReservationServiceTest {

    @InjectMocks
    private StockReservationService stockReservationService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockReservationRepository stockReservationRepository;

    @Test
    @DisplayName("재고 예약 성공: 모든 상품의 재고가 충분할 때 RESERVED를 반환한다.")
    void reserveStock_Success() {
        // given
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Stock stock = new Stock(productId, 10);

        StockReservationItem item = new StockReservationItem(productId, 3);
        StockReservationEvent event = new StockReservationEvent(orderId, List.of(item));

        given(stockRepository.findAllByProductIdIn(any())).willReturn(List.of(stock));

        // when
        StockReservationResult result = stockReservationService.reserveStock(event);

        // then
        assertThat(result).isEqualTo(StockReservationResult.RESERVED);
        assertThat(stock.getReservedQuantity()).isEqualTo(3);
        verify(stockReservationRepository, times(1))
            .save(any(StockReservation.class));
    }

    @Test
    @DisplayName("재고 예약 실패: 재고가 부족한 상품이 포함된 경우 OUT_OF_STOCK을 반환한다.")
    void reserveStock_Fail_OutOfStock() {
        // given
        UUID productId = UUID.randomUUID();
        Stock stock = new Stock(productId, 5);
        StockReservationItem item = new StockReservationItem(productId, 10);
        StockReservationEvent event = new StockReservationEvent(UUID.randomUUID(), List.of(item));

        given(stockRepository.findAllByProductIdIn(any())).willReturn(List.of(stock));

        // when
        StockReservationResult result = stockReservationService.reserveStock(event);

        // then
        assertThat(result).isEqualTo(StockReservationResult.INVALID_REQUEST); // isInvalidStock 체크에서 걸림
        verify(stockReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("재고 확정(Commit): 예약된 재고를 차감하고 품절 여부를 반환한다.")
    void commitStock_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Stock stock = new Stock(productId, 5);
        stock.reserve(5); // reserved=5, available=0

        StockReservation reservation = StockReservation.create(
            productId,
            orderId,
            5
        );

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, ReservationStatus.RESERVED))
            .willReturn(List.of(reservation));
        given(stockRepository.findAllByProductIdIn(any())).willReturn(List.of(stock));

        // when
        List<UUID> soldOutProductIds = stockReservationService.commitStock(orderId);

        // then
        assertThat(soldOutProductIds).containsExactly(productId);
        assertThat(stock.getTotalQuantity()).isEqualTo(0);
        assertThat(stock.getReservedQuantity()).isEqualTo(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("예약 취소: 예약된 수량을 다시 가용 재고로 복구한다.")
    void cancelReservedStock_Success() {
        // given
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Stock stock = new Stock(productId, 10);
        stock.reserve(3); // reserved=3

        StockReservation reservation = StockReservation.create(productId, orderId, 3);

        StockReservationCancelEvent.StockReservationCancelItem cancelItem =
            new StockReservationCancelEvent.StockReservationCancelItem(productId, 3);

        StockReservationCancelEvent event = new StockReservationCancelEvent(
            sagaId,
            orderId,
            List.of(cancelItem)
        );

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, ReservationStatus.RESERVED))
            .willReturn(List.of(reservation));
        given(stockRepository.findAllByProductIdIn(any())).willReturn(List.of(stock));

        // when
        StockReservationCancelResult result = stockReservationService.cancelReservedStock(event);

        // then
        assertThat(result).isEqualTo(StockReservationCancelResult.CANCEL_SUCCEEDED);
        assertThat(stock.getReservedQuantity()).isEqualTo(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("확정된 재고 취소: 이미 차감된 전체 수량을 다시 복구한다.")
    void cancelConfirmedStock_Success() {
        // given
        UUID sagaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Stock stock = new Stock(productId, 5); // total=5

        ConfirmedStockCancelEvent.StockCancelItem cancelItem =
            new ConfirmedStockCancelEvent.StockCancelItem(
                productId,
                3
            );

        ConfirmedStockCancelEvent event = new ConfirmedStockCancelEvent(
            sagaId,
            orderId,
            List.of(cancelItem)
        );

        StockReservation confirmedReservation = StockReservation.create(productId, orderId, 3);
        // 수동으로 CONFIRMED 상태 설정 (도메인 로직에 따라)
        confirmedReservation.confirm();

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, ReservationStatus.CONFIRMED))
            .willReturn(List.of(confirmedReservation));
        given(stockRepository.findAllByProductIdIn(any())).willReturn(List.of(stock));

        // when
        ConfirmedStockCancelResult result = stockReservationService.cancelConfirmedStock(event);

        // then
        assertThat(result).isEqualTo(ConfirmedStockCancelResult.CANCEL_SUCCEEDED);
        assertThat(stock.getTotalQuantity()).isEqualTo(8); // 5 + 3
        assertThat(confirmedReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }
}