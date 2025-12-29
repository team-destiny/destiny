package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.event.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.event.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.event.reservation.StockReservationItem;
import com.destiny.stockservice.domain.entity.ReservationStatus;
import com.destiny.stockservice.domain.entity.Stock;
import com.destiny.stockservice.domain.entity.StockReservation;
import com.destiny.stockservice.domain.repository.StockRepository;
import com.destiny.stockservice.domain.repository.StockReservationRepository;
import com.destiny.stockservice.domain.result.ConfirmedStockCancelResult;
import com.destiny.stockservice.domain.result.StockReservationCancelResult;
import com.destiny.stockservice.domain.result.StockReservationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockReservationService {

    private final StockRepository stockRepository;

    private final StockReservationRepository stockReservationRepository;

    @Transactional
    public StockReservationResult reserveStock(StockReservationEvent event) {

        List<StockReservationItem> reservationItems = event.items();

        Map<UUID, Integer> orderedQuantityByProduct = new java.util.HashMap<>();

        for (StockReservationItem item : reservationItems) {
            if (item.orderedQuantity() == null) {
                return StockReservationResult.INVALID_REQUEST;
            }
            orderedQuantityByProduct.merge(
                item.productId(),
                item.orderedQuantity(),
                Integer::sum
            );
        }

        List<UUID> productIds = new ArrayList<>(orderedQuantityByProduct.keySet());

        Map<UUID, Stock> stockMap = stockRepository.findAllByProductIdIn(productIds)
            .stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));

        for (Map.Entry<UUID, Integer> entry : orderedQuantityByProduct.entrySet()) {
            if (isInvalidStock(entry.getKey(), entry.getValue(), stockMap)) {
                return StockReservationResult.INVALID_REQUEST;
            }
        }

        for (Map.Entry<UUID, Integer> entry : orderedQuantityByProduct.entrySet()) {
            Stock stock = stockMap.get(entry.getKey());

            StockReservationResult result = stock.reserve(entry.getValue());

            if (result != StockReservationResult.RESERVED) {
                return result;
            }

            StockReservation reservation = StockReservation.create(
                entry.getKey(),
                event.orderId(),
                entry.getValue()
            );

            stockReservationRepository.save(reservation);
        }

        return StockReservationResult.RESERVED;
    }

    private boolean isInvalidStock(
        UUID productId,
        Integer orderedQuantity,
        Map<UUID, Stock> stockMap
    ) {
        Stock stock = stockMap.get(productId);

        if (stock == null || stock.getTotalQuantity() == null || orderedQuantity == null) {
            return true;
        }

        return stock.getAvailableQuantity() < orderedQuantity;
    }

    @Transactional
    public List<UUID> commitStock(UUID orderId) {

        List<StockReservation> stockReservations = stockReservationRepository
            .findAllByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        stockReservations.forEach(StockReservation::confirm);

        Map<UUID, Integer> reservedQuantityByProduct =
            groupReservedQuantityByProduct(stockReservations);

        Map<UUID, Stock> stockMap = findStocks(
            new ArrayList<>(reservedQuantityByProduct.keySet())
        );

        List<UUID> soldOutProductIds = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : reservedQuantityByProduct.entrySet()) {
            Stock stock = stockMap.get(entry.getKey());

            if (stock == null) {
                throw new IllegalStateException(
                    "Stock not found for productId=" + entry.getKey()
                );
            }

            stock.commitReservation(entry.getValue());

            if (stock.isSoldOut()) {
                soldOutProductIds.add(entry.getKey());
            }
        }

        return soldOutProductIds;
    }

    @Transactional
    public StockReservationCancelResult cancelReservedStock(StockReservationCancelEvent event) {

        List<StockReservation> reservations = stockReservationRepository
            .findAllByOrderIdAndStatus(event.orderId(), ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            return StockReservationCancelResult.NO_RESERVATION;
        }

        Map<UUID, Integer> cancelQuantityByProduct =
            groupReservedQuantityByProduct(reservations);

        Map<UUID, Stock> stockMap =
            findStocks(cancelQuantityByProduct.keySet().stream().toList());

        cancelReservations(reservations);

        return restoreStock(stockMap, cancelQuantityByProduct);
    }

    private Map<UUID, Integer> groupReservedQuantityByProduct(
        List<StockReservation> reservations
    ) {
        return reservations.stream()
            .collect(Collectors.groupingBy(
                StockReservation::getProductId,
                Collectors.summingInt(StockReservation::getReservedQuantity)
            ));
    }

    private Map<UUID, Stock> findStocks(List<UUID> productIds) {
        return stockRepository.findAllByProductIdIn(productIds).stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));
    }

    private void cancelReservations(List<StockReservation> reservations) {
        reservations.forEach(StockReservation::cancel);
    }

    private StockReservationCancelResult restoreStock(
        Map<UUID, Stock> stockMap,
        Map<UUID, Integer> cancelQuantityByProduct
    ) {
        for (Map.Entry<UUID, Integer> entry : cancelQuantityByProduct.entrySet()) {
            Stock stock = stockMap.get(entry.getKey());

            if (stock == null) {
                return StockReservationCancelResult.NO_RESERVATION;
            }

            stock.cancelReservation(entry.getValue());
        }

        return StockReservationCancelResult.CANCEL_SUCCEEDED;
    }

    @Transactional
    public ConfirmedStockCancelResult cancelConfirmedStock(ConfirmedStockCancelEvent event) {

        Map<UUID, Integer> cancelQuantityByProduct =
            groupConfirmedCancelQuantityByProduct(event.items());

        List<UUID> productIds = new ArrayList<>(cancelQuantityByProduct.keySet());

        List<StockReservation> reservations =
            stockReservationRepository.findAllByOrderIdAndStatus(
                event.orderId(),
                ReservationStatus.CONFIRMED
            );

        if (reservations.isEmpty()) {
            return ConfirmedStockCancelResult.NO_RESERVATION;
        }

        reservations.forEach(StockReservation::cancelConfirmed);

        Map<UUID, Stock> stockMap = findStocks(productIds);

        for (Map.Entry<UUID, Integer> entry : cancelQuantityByProduct.entrySet()) {
            Stock stock = stockMap.get(entry.getKey());

            if (stock == null) {
                return ConfirmedStockCancelResult.NO_RESERVATION;
            }

            stock.restoreConfirmed(entry.getValue());
        }

        return ConfirmedStockCancelResult.CANCEL_SUCCEEDED;
    }

    private Map<UUID, Integer> groupConfirmedCancelQuantityByProduct(
        List<ConfirmedStockCancelEvent.StockCancelItem> items
    ) {
        return items.stream()
            .collect(Collectors.groupingBy(
                ConfirmedStockCancelEvent.StockCancelItem::productId,
                Collectors.summingInt(ConfirmedStockCancelEvent.StockCancelItem::stock)
            ));
    }
}
