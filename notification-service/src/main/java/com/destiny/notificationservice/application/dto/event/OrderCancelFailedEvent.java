package com.destiny.notificationservice.application.dto.event;

import java.util.UUID;

public record OrderCancelFailedEvent(

    UUID orderId,
    UUID userId,
    String failStep,
    String failReason
) {}
