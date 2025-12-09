package com.destiny.sagaorchestrator.infrastructure.messaging.event.request;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestEvent(
    UUID cartId,
    UUID orderId,
    UUID userId,
    UUID couponId,
    List<OrderItemCreateRequestEvent> items
) {

    public static record OrderItemCreateRequestEvent(
        UUID productId,
        UUID itemPromotionId,
        Integer stock
    ) {

    }

}
