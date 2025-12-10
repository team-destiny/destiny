package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.List;
import java.util.UUID;

public record OrderForBrandResponse(
    UUID orderId,
    OrderStatus orderStatus,
    String recipientName,
    String recipientPhone,
    String zipcode,
    String address1,
    String address2,
    String deliveryMessage,
    String paymentMethod,
    List<OrderItemForBrandResponse> items
) {

    public static OrderForBrandResponse from(Order order) {
        return new OrderForBrandResponse(
            order.getOrderId(),
            order.getOrderStatus(),
            order.getRecipientName(),
            order.getRecipientPhone(),
            order.getZipcode(),
            order.getAddress1(),
            order.getAddress2(),
            order.getDeliveryMessage(),
            order.getPaymentMethod(),
            order.getItems().stream()
                .map(OrderItemForBrandResponse::from)
                .toList()
        );
    }

}
