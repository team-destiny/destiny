package com.destiny.stockservice.domain.repository;

import com.destiny.stockservice.domain.entity.Stock;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {
    Optional<Stock> findByProductId(UUID productId);

    Stock save(Stock entity);
}
