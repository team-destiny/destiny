package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.stock.StockCreateEvent;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void createStock(StockCreateEvent event) {

        Objects.requireNonNull(event, "event must not be null");

        Objects.requireNonNull(event.productId(), "event.productId must not be null");

        if (stockRepository.findByProductId(event.productId()).isPresent()) {
            throw new IllegalStateException(
                "해당 상품의 재고 정보가 이미 존재합니다." + event.productId()
            );
        }

        stockRepository.save(event.toEntity());
    }
}
