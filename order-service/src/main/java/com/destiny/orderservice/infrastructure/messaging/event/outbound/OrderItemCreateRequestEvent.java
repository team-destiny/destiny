package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import com.destiny.orderservice.domain.entity.OrderItem;
import java.util.UUID;

public record OrderItemCreateRequestEvent(
    UUID productId,
    UUID itemPromotionId,
    Integer stock
) {
    public static OrderItemCreateRequestEvent from(OrderItem item) {
        return new OrderItemCreateRequestEvent(
            item.getProductId(),
            item.getItemPromotionId(),
            item.getStock()
        );
    }
}
