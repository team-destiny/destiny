package com.destiny.stockservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
public class StockReservation {

    @Id
    private UUID reservationId;

    private UUID orderId;

    private UUID productId;

    private Integer reservedQuantity;

    @Version
    private Long version;
}
