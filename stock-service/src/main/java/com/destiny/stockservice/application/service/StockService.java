package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.StockReduceItem;
import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public boolean validateAndDecrease(List<StockReduceItem> items) {

        for (StockReduceItem item : items) {

            UUID productId = item.productId();
            Integer amount = item.stock();

            if (amount == null || amount <= 0) {
                return false;
            }

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            if (stock.getQuantity() < amount) {
                return false;
            }
        }

        for (StockReduceItem item : items) {

            UUID productId = item.productId();
            Integer amount = item.stock();

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            stock.tryReduceQuantity(amount);
        }

        return true;
    }

    @Transactional
    public void rollbackQuantity(List<StockReduceItem> items) {

        for (StockReduceItem item : items) {

            UUID productId = item.productId();
            Integer amount = item.stock();

            if (amount == null || amount < 0) {
                continue;
            }

            Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow();

            stock.addQuantity(amount);
        }
    }
}
