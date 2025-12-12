package com.destiny.notificationservice.application.dto.event;

import java.util.UUID;

public record SagaCreateFailedEvent(
    UUID orderId,
    String failStep,
    String errorCode,
    String failReason,
    String detailMessage,
    String failService
) {}
