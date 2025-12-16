package com.destiny.stockservice.infrastructure.repository;

import com.destiny.stockservice.domain.entity.StockReservation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {

    List<StockReservation> findAllByOrderId(UUID orderId);
}
