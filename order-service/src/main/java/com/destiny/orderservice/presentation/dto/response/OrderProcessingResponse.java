package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.UUID;

public record OrderProcessingResponse(
    UUID orderId,
    OrderStatus status,
    String message
) {

    public static OrderProcessingResponse of(UUID orderId, OrderStatus status) {
        return new OrderProcessingResponse(
            orderId,
            status,
            OrderStatusMessage.getMessage(status));
    }
}
