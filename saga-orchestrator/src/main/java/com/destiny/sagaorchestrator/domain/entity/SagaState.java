package com.destiny.sagaorchestrator.domain.entity;

import com.destiny.sagaorchestrator.infrastructure.messaging.event.result.ProductValidateSuccessResult;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "p_saga_state")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class SagaState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sagaId;

    private UUID cartId;
    private UUID orderId;
    private UUID userId;
    private UUID couponId;
    private Integer originalAmount;
    private Integer discountAmount;
    private Integer finalAmount;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    @Enumerated(EnumType.STRING)
    private SagaStep step;

    private String failureStep;
    private String failureReason;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<UUID, ProductValidateSuccessResult> productResults = new HashMap<>();

    private String paymentMethod;
    private boolean paymentValid;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateOriginalAmount(Integer originalAmount) {
        this.originalAmount = originalAmount;
    }

    public void updateFinalAmount(Integer finalAmount) {
        this.finalAmount = finalAmount;
    }

    public void updateDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void updatePaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void updatePaymentValid(boolean paymentValid) {
        this.paymentValid = paymentValid;
    }

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
        UUID cartId,
        UUID orderId,
        UUID userId,
        UUID couponId
    ) {
        SagaState sagaState = new SagaState();
        sagaState.cartId = cartId;
        sagaState.orderId = orderId;
        sagaState.userId = userId;
        sagaState.couponId = couponId;
        sagaState.status = SagaStatus.CREATED;
        sagaState.step = SagaStep.ORDER_CREATED;
        return sagaState;
    }
}
