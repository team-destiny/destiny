package com.destiny.notificationservice.application.dto.event;

import java.util.UUID;

public record OrderCancelRequestedEvent(

    UUID orderId,
    UUID userId,
    Integer finalAmount,
    String message

) {}
