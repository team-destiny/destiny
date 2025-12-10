package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderItemStatus;
import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.UUID;

public record OrderItemForBrandResponse(

    UUID orderItemId,
    UUID productId,
    Integer stock,
    Integer unitPrice,
    Integer finalPrice,
    Integer itemDiscountAmount,
    OrderItemStatus status
) {

    public static OrderItemForBrandResponse from(OrderItem item) {

        return new  OrderItemForBrandResponse(
            item.getOrderItemId(),
            item.getProductId(),
            item.getStock(),
            item.getUnitPrice(),
            item.getFinalPrice(),
            item.getItemDiscountAmount(),
            item.getStatus()
        );
    }
}
