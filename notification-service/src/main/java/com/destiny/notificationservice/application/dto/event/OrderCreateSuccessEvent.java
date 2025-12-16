package com.destiny.notificationservice.application.dto.event;

import java.util.List;
import java.util.UUID;

public record OrderCreateSuccessEvent(
    UUID orderId,
    UUID userId,
    List<OrderItem> items
) {
    public record OrderItem(
        UUID productId,
        UUID brandId,
        Integer stock,
        Integer finalAmount
    ) {}
}
