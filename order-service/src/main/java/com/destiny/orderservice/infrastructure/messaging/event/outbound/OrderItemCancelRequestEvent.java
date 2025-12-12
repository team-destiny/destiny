package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import com.destiny.orderservice.domain.entity.OrderItem;
import java.util.UUID;

public record OrderItemCancelRequestEvent(
    UUID productId,
    UUID itemPromotionId,
    Integer stock,
    Integer unitPrice,
    Integer itemDiscountAmount,
    Integer finalPrice
) {

    public static OrderItemCancelRequestEvent from(OrderItem item) {
        return new OrderItemCancelRequestEvent(
            item.getProductId(),
            item.getItemPromotionId(),
            item.getStock(),
            item.getUnitPrice(),
            item.getItemDiscountAmount(),
            item.getFinalPrice()
        );
    }

}
