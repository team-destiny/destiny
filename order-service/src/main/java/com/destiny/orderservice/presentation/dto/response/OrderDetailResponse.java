package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
    UUID orderId,
    UUID userId,
    UUID couponId,
    Integer originalAmount,
    Integer finalAmount,
    Integer discountAmount,
    OrderStatus orderStatus,
    String paymentMethod,
    String recipientName,
    String recipientPhone,
    String zipcode,
    String address1,
    String address2,
    String deliveryMessage,
    List<OrderItemResponse> items
) {

    public static OrderDetailResponse fromEntity(Order order) {
        return new OrderDetailResponse(
            order.getOrderId(),
            order.getUserId(),
            order.getCouponId(),
            order.getOriginalAmount(),
            order.getFinalAmount(),
            order.getDiscountAmount(),
            order.getOrderStatus(),
            order.getPaymentMethod(),
            order.getRecipientName(),
            order.getRecipientPhone(),
            order.getZipcode(),
            order.getAddress1(),
            order.getAddress2(),
            order.getDeliveryMessage(),
            order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .toList()
        );
    }

}
