package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import com.destiny.orderservice.domain.entity.Order;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequestEvent(
    UUID orderId,
    UUID userId,
    List<OrderItemCreateRequestEvent> items
) {
    public static OrderCreateRequestEvent from(Order order) {
        return new OrderCreateRequestEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getItems().stream()
                .map(OrderItemCreateRequestEvent::from)
                .toList()
        );
    }

}
