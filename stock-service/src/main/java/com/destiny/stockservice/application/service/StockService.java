package com.destiny.stockservice.application.service;

import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public boolean validateAndDecrease(Map<UUID, Integer> orderedProducts) {

        for (Map.Entry<UUID, Integer> entry : orderedProducts.entrySet()) {
            UUID productId = entry.getKey();
            Integer amount = entry.getValue();

            if (amount == null) {
                return false;
            }

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            if (stock.getQuantity() < amount) {
                return false;
            }
        }

        for (Map.Entry<UUID, Integer> entry : orderedProducts.entrySet()) {
            UUID productId = entry.getKey();
            Integer amount = entry.getValue();

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            stock.tryReduceQuantity(amount);
        }

        return true;
    }

    @Transactional
    public void rollbackQuantity(Map<UUID, Integer> orderedProducts) {
        for (Map.Entry<UUID, Integer> entry : orderedProducts.entrySet()) {
            UUID productId = entry.getKey();
            Integer amount = entry.getValue();

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            stock.addQuantity(amount);
        }
    }
}
