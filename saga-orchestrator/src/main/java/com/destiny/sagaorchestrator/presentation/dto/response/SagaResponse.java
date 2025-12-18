package com.destiny.sagaorchestrator.presentation.dto.response;

import com.destiny.sagaorchestrator.domain.entity.SagaState;
import com.destiny.sagaorchestrator.domain.entity.SagaStatus;
import com.destiny.sagaorchestrator.domain.entity.SagaStep;
import java.time.LocalDateTime;
import java.util.UUID;

public record SagaResponse(

    UUID sagaId,
    UUID userId,
    Integer finalAmount,
    SagaStatus status,
    SagaStep step,
    String failureReason,
    LocalDateTime createdAt

) {

    public static SagaResponse from(SagaState sagaState) {
        return new SagaResponse(
            sagaState.getSagaId(),
            sagaState.getUserId(),
            sagaState.getFinalAmount(),
            sagaState.getStatus(),
            sagaState.getStep(),
            sagaState.getFailureReason(),
            sagaState.getCreatedAt()
        );
    }
}
