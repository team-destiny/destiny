package com.destiny.stockservice.application.service;

import com.destiny.stockservice.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockReservationService {

    private final StockRepository stockRepository;

    @Transactional
    public void reserveStock() {

    }

    @Transactional
    public void commitStock() {

    }
}
