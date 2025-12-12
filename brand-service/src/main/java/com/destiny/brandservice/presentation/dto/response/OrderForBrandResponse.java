package com.destiny.brandservice.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record OrderForBrandResponse(
    UUID orderId,
    String orderStatus,
    String recipientName,
    String recipientPhone,
    String zipCode,
    String address1,
    String address2,
    String deliveryMessage,
    String paymentMethod,
    List<OrderItemForBrandResponse> items
) {

}
