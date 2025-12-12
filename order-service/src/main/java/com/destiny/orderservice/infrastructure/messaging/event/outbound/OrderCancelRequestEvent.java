package com.destiny.orderservice.infrastructure.messaging.event.outbound;

import com.destiny.orderservice.domain.entity.Order;
import java.util.List;
import java.util.UUID;

public record OrderCancelRequestEvent(
    UUID orderId,
    UUID userId,
    UUID couponId,
    Integer originalAmount,
    Integer finalAmount,
    Integer discountAmount,
    List<OrderItemCancelRequestEvent> items
) {

    public static OrderCancelRequestEvent from(Order order) {
        return new OrderCancelRequestEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            order.getOriginalAmount(),
            order.getFinalAmount(),
            order.getDiscountAmount(),
            order.getItems().stream()
                .map(OrderItemCancelRequestEvent::from)
                .toList()
        );
    }

}
