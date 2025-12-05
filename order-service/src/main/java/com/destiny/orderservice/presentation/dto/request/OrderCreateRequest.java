package com.destiny.orderservice.presentation.dto.request;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
    UUID cartId,
    UUID couponId,
    String paymentMethod,
    String recipientName,
    String recipientPhone,
    String zipcode,
    String address1,
    String address2,
    String deliveryMessage,
    List<OrderItemCreateRequest> items
) {

    public static record OrderItemCreateRequest(
        UUID productId,
        UUID itemPromotionId,
        Integer stock
    ) {

    }

}
