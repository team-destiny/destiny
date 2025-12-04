package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderItemStatus;
import java.util.UUID;

public record OrderListItemResponse(
    UUID orderItemId,
    UUID productId,
    UUID itemPromotionId,
    UUID brandId,
    Integer stock,
    OrderItemStatus status
) {

    public static OrderListItemResponse from(OrderItem item) {
        return new OrderListItemResponse(
            item.getOrderItemId(),
            item.getProductId(),
            item.getItemPromotionId(),
            item.getBrandId(),
            item.getStock(),
            item.getStatus()
        );
    }

}
