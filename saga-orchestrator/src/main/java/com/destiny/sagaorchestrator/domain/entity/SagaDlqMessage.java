package com.destiny.sagaorchestrator.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_saga_dlq_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaDlqMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String originalTopic;

    @Column(nullable = false, length = 255)
    private String dlqTopic;

    @Column(nullable = false)
    private Integer partitionNumber;

    @Column(nullable = false)
    private Integer offsetNumber;

    @Column(nullable = false, length = 255)
    private String consumerGroup;

    @Column
    private String messageKey;

    @Column(nullable = false)
    private String messagePayload;

    @Column(nullable = false, length = 255)
    private String exceptionType;

    @Column(length = 1000)
    private String exceptionMessage;

    @Column(length = 4000)
    private String stacktrace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SagaDlqStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    public static SagaDlqMessage of(
        String originalTopic,
        String dlqTopic,
        Integer partitionNumber,
        Integer offsetNumber,
        String consumerGroup,
        String messageKey,
        String messagePayload,
        String exceptionMessage,
        String stacktrace
    ) {
        SagaDlqMessage message = new SagaDlqMessage();
        message.originalTopic = originalTopic;
        message.dlqTopic = dlqTopic;
        message.partitionNumber = partitionNumber;
        message.offsetNumber = offsetNumber;
        message.consumerGroup = consumerGroup;
        message.messageKey = messageKey;
        message.messagePayload = messagePayload;
        message.exceptionMessage = exceptionMessage;
        message.stacktrace = stacktrace;
        message.status = SagaDlqStatus.PENDING;
        message.retryCount = 0;
        message.createdAt = LocalDateTime.now();
        return message;
    }

    public void markRetry() {
        this.status = SagaDlqStatus.RETRY;
        this.retryCount++;
        this.processedAt = LocalDateTime.now();
    }

    public void markDropped() {
        this.status = SagaDlqStatus.DROPPED;
        this.processedAt = LocalDateTime.now();
    }

}
