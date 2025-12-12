package com.destiny.brandservice.presentation.dto.response;

import java.util.UUID;

public record OrderItemForBrandResponse(

    UUID orderItemId,
    UUID productId,
    Integer stock,
    Integer unitPrice,
    Integer finalPrice,
    Integer itemDiscountAmount,
    String status
) { }
