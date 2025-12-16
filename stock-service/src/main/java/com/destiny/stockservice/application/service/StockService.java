package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.StockCreateEvent;
import com.destiny.stockservice.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void createStock(StockCreateEvent message) {

        if (stockRepository.findByProductId(message.productId()).isPresent()) {
            throw new IllegalStateException(
                "해당 상품의 재고 정보가 이미 존재합니다." + message.productId()
            );
        }

        stockRepository.save(message.toEntity());
    }
}
