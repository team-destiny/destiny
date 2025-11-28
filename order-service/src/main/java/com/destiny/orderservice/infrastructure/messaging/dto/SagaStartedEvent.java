package com.destiny.orderservice.infrastructure.messaging.dto;

import com.destiny.orderservice.domain.entity.Order;
import java.util.List;
import java.util.UUID;

public record SagaStartedEvent(
    UUID orderId,
    UUID userId,
    List<SagaOrderItem> items
) {
    public static SagaStartedEvent from(Order order) {
        return new SagaStartedEvent(
            order.getOrderId(),
            order.getUserId(),
            order.getItems().stream()
                .map(SagaOrderItem::from)
                .toList()
        );
    }

}
