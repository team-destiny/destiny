package com.destiny.stockservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.destiny.stockservice.application.dto.stock.StockCreateEvent;
import com.destiny.stockservice.application.service.StockService;
import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Test
    @DisplayName("새로운 상품의 재고를 생성한다.")
    void createStock_Success() {
        // given
        UUID productId = UUID.randomUUID();
        StockCreateEvent event = new StockCreateEvent(productId, 100);

        given(stockRepository.findByProductId(productId)).willReturn(Optional.empty());

        // when
        stockService.createStock(event);

        // then
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    @DisplayName("이미 존재하는 상품의 재고를 생성시 예외가 발생한다.")
    void createStock_Fail_AlreadyExists() {
        // given
        UUID productId = UUID.randomUUID();
        StockCreateEvent event = new StockCreateEvent(productId, 100);
        Stock existingStock = new Stock(productId, 50);

        given(stockRepository.findByProductId(productId)).willReturn(Optional.of(existingStock));

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            stockService.createStock(event);
        });

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("재고 생성시 이벤트 객체가 null이면 NPE가 발생한다.")
    void createStock_Fail_NullEvent() {
        // when & then
        assertThrows(NullPointerException.class, () -> {
            stockService.createStock(null);
        });
    }
}