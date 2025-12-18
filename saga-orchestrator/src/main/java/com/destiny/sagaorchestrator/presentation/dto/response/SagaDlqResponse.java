package com.destiny.sagaorchestrator.presentation.dto.response;

import com.destiny.sagaorchestrator.domain.entity.SagaDlqMessage;
import com.destiny.sagaorchestrator.domain.entity.SagaDlqStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record SagaDlqResponse(
    UUID dlqId,
    String originalTopic,
    String dlqTopic,
    Integer partitionNumber,
    Long offsetNumber,
    String consumerGroup,
    String messageKey,
    String messagePayload,
    String exceptionType,
    String exceptionMessage,
    SagaDlqStatus status,
    Integer retryCount,
    LocalDateTime createdAt
) {

    public static SagaDlqResponse from(SagaDlqMessage message) {
        return new SagaDlqResponse(
            message.getId(),
            message.getOriginalTopic(),
            message.getDlqTopic(),
            message.getPartitionNumber(),
            message.getOffsetNumber(),
            message.getConsumerGroup(),
            message.getMessageKey(),
            message.getMessagePayload(),
            message.getExceptionType(),
            message.getExceptionMessage(),
            message.getStatus(),
            message.getRetryCount(),
            message.getCreatedAt()
        );
    }
}
