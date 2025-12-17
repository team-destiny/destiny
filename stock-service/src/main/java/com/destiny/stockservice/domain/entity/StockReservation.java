package com.destiny.stockservice.domain.entity;

import com.destiny.stockservice.domain.result.ConfirmedStockCancelResult;
import com.destiny.stockservice.domain.result.StockReservationCancelResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reservationId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    private Integer reservedQuantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Version
    private Long version;

    public static StockReservation create(UUID productId, UUID orderId, int reservedQuantity) {
        return new StockReservation(
            null,
            orderId,
            productId,
            reservedQuantity,
            ReservationStatus.RESERVED,
            0L
        );
    }

    public StockReservationCancelResult cancel() {
        if (status == ReservationStatus.CANCELED) {
            return StockReservationCancelResult.ALREADY_CANCELED;
        }

        if (status != ReservationStatus.RESERVED) {
            return StockReservationCancelResult.NO_RESERVATION;
        }

        status = ReservationStatus.CANCELED;

        return StockReservationCancelResult.CANCEL_SUCCEEDED;
    }

    public ConfirmedStockCancelResult cancelConfirmed() {
        if (status == ReservationStatus.CANCELED) {
            return ConfirmedStockCancelResult.CANCEL_FAILED;
        }

        if (status != ReservationStatus.CONFIRMED) {
            return ConfirmedStockCancelResult.NO_RESERVATION;
        }

        status = ReservationStatus.CANCELED;
        return ConfirmedStockCancelResult.CANCEL_SUCCEEDED;
    }

    public void confirm() {
        if (status != ReservationStatus.RESERVED) {
            return;
        }

        status = ReservationStatus.CONFIRMED;
    }
}
