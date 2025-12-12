package com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome;

import java.util.List;
import java.util.UUID;

public record OrderCreateSuccessEvent(
    UUID orderId,
    UUID userId,
    UUID couponId,
    Integer originalAmount,
    Integer discountAmount,
    Integer finalAmount,
    List<OrderItem> items

) {

    public record OrderItem(
        UUID productId,
        UUID brandId,
        Integer price,
        Integer stock
    ) {

    }

}
