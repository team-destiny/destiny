package com.destiny.stockservice.infrastructure.repository;

import com.destiny.stockservice.domain.entity.Stock;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockJpaRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByProductId(UUID productId);
}
