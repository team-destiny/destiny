package com.destiny.stockservice.domain.repository;

import com.destiny.stockservice.domain.entity.ReservationStatus;
import com.destiny.stockservice.domain.entity.StockReservation;
import java.util.List;
import java.util.UUID;

public interface StockReservationRepository {

    void saveAll(List<StockReservation> reservations);

    List<StockReservation> findAllByOrderIdAndStatus(UUID orderId, ReservationStatus status);
}
