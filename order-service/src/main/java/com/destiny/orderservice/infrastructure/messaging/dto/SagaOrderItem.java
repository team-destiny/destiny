package com.destiny.orderservice.infrastructure.messaging.dto;

import com.destiny.orderservice.domain.entity.OrderItem;
import java.util.UUID;

public record SagaOrderItem(
    UUID productId,
    UUID itemPromotionId,
    Integer stock
) {
    public static SagaOrderItem from(OrderItem item) {
        return new SagaOrderItem(
            item.getProductId(),
            item.getItemPromotionId(),
            item.getStock()
        );
    }
}
