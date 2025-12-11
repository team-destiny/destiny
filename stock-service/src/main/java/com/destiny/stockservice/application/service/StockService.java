package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.StockCreateMessage;
import com.destiny.stockservice.application.dto.StockReduceItem;
import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public boolean validateAndDecrease(List<StockReduceItem> items) {
        List<UUID> productIds = items.stream()
            .map(StockReduceItem::productId)
            .toList();

        Map<UUID, Stock> stockMap = stockRepository
            .findByProductIdIn(productIds)
            .stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));

        for (StockReduceItem item : items) {
            Stock stock = stockMap.get(item.productId());

            if (stock == null
                || stock.getQuantity() == null
                || stock.getQuantity() < item.orderedQuantity()
            ) {
                return false;
            }
        }

        for (StockReduceItem item : items) {
            Stock stock = stockMap.get(item.productId());

            stock.tryReduceQuantity(item.orderedQuantity());
        }

        return true;
    }

    @Transactional
    public void rollbackQuantity(List<StockReduceItem> items) {

        List<UUID> productIds = items.stream()
            .map(StockReduceItem::productId)
            .toList();

        Map<UUID, Stock> stockMap = stockRepository
            .findByProductIdIn(productIds).stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));

        for (StockReduceItem item : items) {
            Integer orderedQuantity = item.orderedQuantity();
            if (orderedQuantity == null || orderedQuantity < 0) {
                continue;
            }

            Stock stock = stockMap.get(item.productId());
            if (stock == null) {
                throw new IllegalStateException("재고 정보가 없습니다. " + item.productId());
            }

            stock.addQuantity(orderedQuantity);
        }
    }

    @Transactional
    public void createStock(StockCreateMessage message) {

        if (stockRepository.findByProductId(message.productId()).isPresent()) {
            throw new IllegalStateException(
                "해당 상품의 재고 정보가 이미 존재합니다." + message.productId()
            );
        }

        stockRepository.save(message.toEntity());
    }
}
