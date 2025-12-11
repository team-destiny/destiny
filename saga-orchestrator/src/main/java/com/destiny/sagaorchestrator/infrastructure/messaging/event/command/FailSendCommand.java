package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.util.UUID;

public record FailSendCommand(
    UUID orderId,
    UUID brandId,
    String failStep,
    String errorCode,
    String failReason,
    String detailMessage,
    String failService
) {

}
