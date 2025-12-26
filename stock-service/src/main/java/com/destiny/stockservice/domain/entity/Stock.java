package com.destiny.stockservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.stockservice.domain.result.StockReservationResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Version
    private Long version;

    public Stock(UUID productId, Integer quantity) {

        this.productId = Objects.requireNonNull(productId, "productId must not be null");

        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }

        this.totalQuantity = quantity;

        this.reservedQuantity = 0;
    }

    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public StockReservationResult reserve(int reservationQuantity) {

        if (reservationQuantity <= 0) {
            return StockReservationResult.INVALID_REQUEST;
        }

        if (getAvailableQuantity() < reservationQuantity) {
            return StockReservationResult.OUT_OF_STOCK;
        }

        reservedQuantity += reservationQuantity;

        return StockReservationResult.RESERVED;
    }

    public boolean cancelReservation(int amount) {

        if (amount <= 0) {
            return false;
        }

        if (reservedQuantity < amount) {
            return false;
        }

        reservedQuantity -= amount;

        return true;
    }

    public boolean commitReservation(int amount) {

        if (amount <= 0) {
            return false;
        }

        if (reservedQuantity < amount) {
            return false;
        }

        if (totalQuantity < amount) {
            return false;
        }

        reservedQuantity -= amount;

        totalQuantity -= amount;

        return true;
    }

    public boolean restoreConfirmed(int quantity) {

        if (quantity <= 0) {
            return false;
        }

        this.totalQuantity += quantity;

        return true;
    }

    public boolean isSoldOut() {
        return getAvailableQuantity() == 0;
    }
}
