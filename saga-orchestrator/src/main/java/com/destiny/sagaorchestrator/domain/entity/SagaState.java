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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "p_saga_state")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SagaState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sagaId;

    private UUID orderId;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    @Enumerated(EnumType.STRING)
    private SagaStep step;

    private String failureStep;

    private String failureReason;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateStatus(SagaStatus status) {
        this.status = status;
    }

    public void updateStep(SagaStep step) {
        this.step = step;
    }

    public void updateFailureStep(String failureStep) {
        this.failureStep = failureStep;
    }

    public void updateFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public static SagaState of(
        UUID orderId,
        UUID userId
    ) {
        SagaState sagaState = new SagaState();
        sagaState.orderId = orderId;
        sagaState.userId = userId;
        sagaState.status = SagaStatus.CREATED;
        sagaState.step = SagaStep.ORDER_CREATED;
        return sagaState;
    }
}
