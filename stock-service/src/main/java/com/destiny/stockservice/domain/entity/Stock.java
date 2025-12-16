package com.destiny.stockservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
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

    private UUID productId;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Version
    private Long version;

    public Stock(UUID productId, Integer quantity) {
        this.productId = productId;
        this.totalQuantity = quantity;
        this.reservedQuantity = 0;
    }

    public void tryReduceQuantity(Integer amount) {
        if (totalQuantity < amount) {
            return;
        }

        totalQuantity -= amount;
    }

    public void addQuantity(Integer amount) {
        totalQuantity += amount;
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

    public void cancelReservation(int amount) {
        if (reservedQuantity < amount) {
            throw new IllegalStateException("Invalid reservation cancel");
        }

        reservedQuantity -= amount;
    }

    public void commitReservation(int amount) {
        if (reservedQuantity < amount) {
            throw new IllegalStateException("Not enough reserved stock");
        }

        reservedQuantity -= amount;

        totalQuantity -= amount;

        if (totalQuantity < 0) {
            throw new IllegalStateException("Stock became negative");
        }
    }

    public boolean isSoldOut() {
        return totalQuantity == 0;
    }
}
