package com.destiny.paymentservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    @NotNull
    private UUID userId;

    // PG사 거래 고유 식별자
    private String pgTxId;

    // PG사 종류 (TOSSPAYMENTS, BOOTPAY, PORTONE, MOCK)
    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    // 결제수단 카드결제, 계좌이체 등
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    @PositiveOrZero
    @NotNull
    private Integer amount;

    public static Payment of(UUID orderId, UUID userId, Integer amount) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.userId = userId;
        payment.amount = amount;
        payment.paymentStatus = PaymentStatus.PENDING;
        return payment;
    }

    // [검증] 결제 가능 상태인지 확인
    public void validatePayableStatus() {
        if (this.paymentStatus == PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
        }
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    // [검증] 금액 일치 여부 확인
    public void validateAmount(Integer amount) {
        if (amount == null || !this.amount.equals(amount)) {
            throw new BizException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    // [상태 변경] 결제 완료 처리
    public void completePayment(PaymentProvider provider, PaymentMethod method, String pgTxId) {
        this.paymentProvider = provider;
        this.paymentMethod = method;
        this.pgTxId = pgTxId;
        this.paymentStatus = PaymentStatus.PAID;
    }

    // [상태 변경] 결제 취소 처리
    public void cancel() {
        if (this.paymentStatus == PaymentStatus.CANCELED) {
            throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_CANCELED);
        }
        if (this.paymentStatus != PaymentStatus.PAID) {
            throw new BizException(PaymentErrorCode.PAYMENT_INVALID_REQUEST);
        }
        this.paymentStatus = PaymentStatus.CANCELED;
    }
}