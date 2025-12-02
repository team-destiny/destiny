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
    OrderItemStatus status,

    UUID orderId,
    OrderStatus orderStatus,
    String recipientName,
    String recipientPhone,
    String zipcode,
    String address1,
    String address2,
    String deliveryMessage,
    String paymentMethod
) {

    public static OrderItemForBrandResponse from(OrderItem item) {
        Order order = item.getOrder();

        return new  OrderItemForBrandResponse(
            item.getOrderItemId(),
            item.getProductId(),
            item.getStock(),
            item.getUnitPrice(),
            item.getFinalPrice(),
            item.getItemDiscountAmount(),
            item.getStatus(),

            order.getOrderId(),
            order.getOrderStatus(),
            order.getRecipientName(),
            order.getRecipientPhone(),
            order.getZipcode(),
            order.getAddress1(),
            order.getAddress2(),
            order.getDeliveryMessage(),
            order.getPaymentMethod()
        );
    }
}
