package com.destiny.notificationservice.application.dto.event;

import java.time.LocalDateTime;

public record NotificationDlqMessageEvent(
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
