package com.destiny.stockservice.application.service;

import com.destiny.stockservice.application.dto.stock.cancel.ConfirmedStockCancelEvent;
import com.destiny.stockservice.application.dto.stock.cancel.StockReservationCancelEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationEvent;
import com.destiny.stockservice.application.dto.stock.reservation.StockReservationItem;
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

        List<UUID> productIds = reservationItems.stream()
            .map(StockReservationItem::productId)
            .toList();

        Map<UUID, Stock> stockMap = stockRepository.findAllByProductIdIn(productIds)
            .stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));

        for (StockReservationItem item : reservationItems) {
            if (isInvalidStock(item, stockMap)) {
                return StockReservationResult.INVALID_REQUEST;
            }
        }

        for (StockReservationItem item : reservationItems) {
            Stock stock = stockMap.get(item.productId());

            StockReservationResult result = stock.reserve(item.orderedQuantity());

            if (result != StockReservationResult.RESERVED) {
                return result;
            }

            StockReservation reservation = StockReservation.create(
                item.productId(),
                event.orderId(),
                item.orderedQuantity()
            );

            stockReservationRepository.save(reservation);
        }

        return StockReservationResult.RESERVED;
    }

    private boolean isInvalidStock(StockReservationItem item, Map<UUID, Stock> stockMap) {
        Stock stock = stockMap.get(item.productId());

        if (stock == null) {
            return true;
        }

        if (stock.getTotalQuantity() == null) {
            return true;
        }

        if (item.orderedQuantity() == null) {
            return true;
        }

        return stock.getAvailableQuantity() < item.orderedQuantity();
    }

    @Transactional
    public List<UUID> commitStock(UUID orderId) {

        List<StockReservation> stockReservations = stockReservationRepository
            .findAllByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);

        stockReservations.forEach(StockReservation::confirm);

        List<UUID> productIds = stockReservations.stream()
            .map(StockReservation::getProductId)
            .toList();

        Map<UUID, Integer> reservedQuantityByProduct =
            stockReservations.stream()
                .collect(Collectors.groupingBy(
                    StockReservation::getProductId,
                    Collectors.summingInt(StockReservation::getReservedQuantity)
                ));

        Map<UUID, Stock> stockMap = stockRepository
            .findAllByProductIdIn(productIds)
            .stream()
            .collect(Collectors.toMap(Stock::getProductId, s -> s));

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
            .findAllByOrderIdAndStatus(event.orderId(), ReservationStatus.CANCELED);

        if (reservations.isEmpty()) {
            return StockReservationCancelResult.NO_RESERVATION;
        }

        Map<UUID, Integer> cancelQuantityByProduct =
            groupCancelQuantityByProduct(reservations);

        Map<UUID, Stock> stockMap =
            findStocks(cancelQuantityByProduct.keySet().stream().toList());

        cancelReservations(reservations);

        return restoreStock(stockMap, cancelQuantityByProduct);
    }

    private Map<UUID, Integer> groupCancelQuantityByProduct(
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
    public ConfirmedStockCancelResult cancelConfirmedStock(
        ConfirmedStockCancelEvent event
    ) {
        Map<UUID, Integer> cancelQuantityByProduct =
            event.items().stream()
                .collect(Collectors.toMap(
                    ConfirmedStockCancelEvent.StockCancelItem::productId,
                    ConfirmedStockCancelEvent.StockCancelItem::stock
                ));

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
}
