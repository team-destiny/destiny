package com.destiny.paymentservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.domain.vo.PaymentType;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    // PG사 거래 고유 식별자 (MOCK 결제 시 null)
    private String pgTxId;

    // PG사 종류 (TOSSPAYMENTS, BOOTPAY, PORTONE, MOCK 등)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    @PositiveOrZero
    @NotNull
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentStatus paymentStatus;


    // =======================================================
    // 1. Static Factory Method (생성 행위)
    // =======================================================

    /**
     * 초기 결제 엔티티를 생성합니다. (기본 상태는 PENDING)
     */
    public static Payment of(UUID orderId, String pgTxId, PaymentType paymentType, Integer amount) {
        if (orderId == null || amount == null || amount < 0) {
            throw new BizException(PaymentErrorCode.PAYMENT_INVALID_REQUEST);
        }

        Payment payment = new Payment();
        PaymentType finalPaymentType = (paymentType == null || !StringUtils.hasText(pgTxId)) ? PaymentType.MOCK : paymentType;

        payment.orderId = orderId;
        payment.pgTxId = pgTxId;
        payment.paymentType = finalPaymentType;
        payment.amount = amount;
        payment.paymentStatus = PaymentStatus.PENDING;

        return payment;
    }

    // =======================================================
    // 2. Domain Behavior Method (상태 변경 행위)
    // =======================================================

    /**
     * 결제 상태를 PAID로 변경하고, PG사 거래 정보를 업데이트합니다.
     */
    public void paid(String pgTxId, PaymentType paymentType) {
        if (this.paymentStatus == PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
        }
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }
        if (!StringUtils.hasText(pgTxId) || paymentType == null) {
            throw new BizException(PaymentErrorCode.PAYMENT_INVALID_REQUEST);
        }

        this.pgTxId = pgTxId;
        this.paymentType = paymentType;
        this.paymentStatus = PaymentStatus.PAID;
    }

    /**
     * 결제를 취소 상태로 변경하고, 금액을 0으로 설정합니다. (전액 취소)
     */
    public void cancel() {
        if (this.paymentStatus == PaymentStatus.CANCELED) {
            throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_CANCELED);
        }
        if (this.paymentStatus != PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_INVALID_REQUEST);
        }

        this.amount = 0; // 실제로 결제된 금액
        this.paymentStatus = PaymentStatus.CANCELED;
    }
}