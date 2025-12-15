package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderStatus;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCreateFailedEvent;
import java.util.UUID;

public record OrderProcessingResponse(
    UUID orderId,
    OrderStatus status,
    String message
) {
    public static OrderProcessingResponse from(Order order) {
        String message;

        if (order.getOrderStatus() == OrderStatus.FAILED) {
            message = order.getFailureReason();
        } else {
            message = OrderStatusMessage.getMessage(order.getOrderStatus());
        }

        return new OrderProcessingResponse(
            order.getOrderId(),
            order.getOrderStatus(),
            message
        );
    }
}
