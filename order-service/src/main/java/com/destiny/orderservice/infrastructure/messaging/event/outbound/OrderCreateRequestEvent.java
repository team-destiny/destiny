package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import com.destiny.orderservice.domain.entity.Order;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequestEvent(
    UUID cartId,
    UUID orderId,
    UUID userId,
    UUID couponId,
    List<OrderItemCreateRequestEvent> items
) {
    public static OrderCreateRequestEvent from(Order order, UUID cartId) {
        return new OrderCreateRequestEvent(
            cartId,
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            order.getItems().stream()
                .map(OrderItemCreateRequestEvent::from)
                .toList()
        );
    }
}
