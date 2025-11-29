package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderItemStatus;
import java.util.UUID;

public record OrderItemResponse(
    UUID orderItemId,
    UUID productId,
    UUID itemPromotionId,
    Integer stock,
    Integer unitPrice,
    Integer finalPrice,
    Integer itemDiscountAmount,
    OrderItemStatus status
) {

    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
            item.getOrderItemId(),
            item.getProductId(),
            item.getItemPromotionId(),
            item.getStock(),
            item.getUnitPrice(),
            item.getFinalPrice(),
            item.getItemDiscountAmount(),
            item.getStatus()
        );
    }
}
