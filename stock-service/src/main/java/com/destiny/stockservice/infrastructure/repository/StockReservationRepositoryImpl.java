package com.destiny.stockservice.infrastructure.repository;

import com.destiny.stockservice.domain.entity.StockReservation;
import com.destiny.stockservice.domain.repository.StockReservationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository stockReservationJpaRepository;

    @Override
    public void save(StockReservation reservation) {
        stockReservationJpaRepository.save(reservation);
    }

    @Override
    public List<StockReservation> findAllByOrderId(UUID orderId) {
        return stockReservationJpaRepository.findALlByOrderId(orderId);
    }
}
