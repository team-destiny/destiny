package com.destiny.sagaorchestrator.infrastructure.messaging.event.command;

import java.time.LocalDateTime;

public record NotificationDlqCommand(
    String originalTopic,
    String dlqTopic,
    Integer partitionNumber,
    Long offsetNumber,
    String consumerGroup,
    String messageKey,
    String messagePayload,
    String exceptionType,
    String status,
    Integer retryCount,
    LocalDateTime createdAt
) {

}
