package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.List;
import java.util.UUID;

public record OrderListResponse(
    UUID orderId,
    OrderStatus orderStatus,
    List<OrderListItemResponse> items
) {

    public static OrderListResponse from(Order order) {
        return new OrderListResponse(
            order.getOrderId(),
            order.getOrderStatus(),
            order.getItems().stream()
                .map(OrderListItemResponse::from)
                .toList()
        );
    }
}
