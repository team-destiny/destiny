package com.destiny.stockservice.infrastructure.repository;

import com.destiny.stockservice.domain.entity.Stock;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockJpaRepository extends JpaRepository<Stock, UUID> {
    Stock findByProductId(UUID productId);
}
