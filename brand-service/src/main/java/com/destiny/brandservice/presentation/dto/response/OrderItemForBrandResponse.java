package com.destiny.brandservice.presentation.dto.response;

import java.util.UUID;

public record OrderItemForBrandResponse(

    UUID orderItemId,
    UUID productId,
    Integer stock,
    Integer unitPrice,
    Integer finalPrice,
    Integer itemDiscountAmount,
    String status,

    UUID orderId,
    String orderStatus,
    String recipientName,
    String recipientPhone,
    String zipcode,
    String address1,
    String address2,
    String deliveryMessage,
    String paymentMethod

) { }
