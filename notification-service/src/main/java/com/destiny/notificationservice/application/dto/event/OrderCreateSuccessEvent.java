package com.destiny.notificationservice.application.dto.event;

import java.util.List;
import java.util.UUID;

public record OrderCreateSuccessEvent(
    UUID orderId,
    UUID userId,

    UUID couponId,
    Integer originalAmount,
    Integer discountAmount,

    List<OrderItem> items,
    Integer finalAmount
) {
    public record OrderItem(
        UUID productId,
        UUID brandId,

        Integer price,

        Integer stock
    ) {}
}
