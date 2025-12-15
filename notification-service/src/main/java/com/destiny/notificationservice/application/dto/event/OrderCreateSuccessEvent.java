package com.destiny.notificationservice.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreateSuccessEvent(
    UUID orderId,
    UUID userId,
    List<OrderItem> items
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderItem(
        UUID productId,
        UUID brandId,
        Integer stock,
        Integer finalAmount
    ) {}
}