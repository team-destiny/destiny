package com.destiny.stockservice.infrastructure.repository;

import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.repository.StockRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Optional<Stock> findByProductId(UUID productId) {
        return stockJpaRepository.findByProductId(productId);
    }

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }
}
