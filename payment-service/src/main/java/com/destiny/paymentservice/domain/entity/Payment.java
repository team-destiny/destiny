package com.destiny.paymentservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.domain.vo.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // 운명팀 고유 결제 ID (UUID)

    @Column(nullable = false, unique = true)
    private String orderId; // 주문 서비스로부터 받은 주문 번호

    @Column(nullable = false)
    // PG사 거래 고유 식별자 (TossPayments의 paymentKey, Bootpay의 receiptId 등)
    private String pgTxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType; // PG사 종류 (TOSSPAYMENTS, BOOTPAY, PORTONE)

    @Column(nullable = false)
    private Long amount; // 최종 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태 (PAID, CANCELED 등)


    // =======================================================
    // 1. Static Factory Method (생성 행위)
    // =======================================================

    /**
     * 성공적으로 PG사 승인이 완료된 결제 엔티티를 생성합니다.
     * 외부에서 인스턴스 생성을 막고, 상태를 PAID로 강제합니다.
     */
    public static Payment createSuccess(
        String orderId,
        String pgTxId,
        PaymentType paymentType,
        Long amount
    ) {
        Payment payment = new Payment();

        // 필수 필드 할당
        payment.orderId = orderId;
        payment.pgTxId = pgTxId;
        payment.paymentType = paymentType;
        payment.amount = amount;

        // 비즈니스 규칙 강제
        payment.paymentStatus = PaymentStatus.PAID;

        return payment;
    }

    // =======================================================
    // 2. Domain Behavior Method (상태 변경 행위)
    // =======================================================

    /**
     * 결제를 취소 상태로 변경하고, 금액을 0으로 설정합니다.
     */
    public void cancel() {
        if (this.paymentStatus == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        if (this.paymentStatus != PaymentStatus.PAID) {
            throw new IllegalStateException("승인된 결제만 취소할 수 있습니다. 현재 상태: " + this.paymentStatus);
        }

        this.paymentStatus = PaymentStatus.CANCELED;
        // 취소 금액은 PG사에서 관리하지만, 우리 시스템에서는 최종 금액을 0으로 볼 수 있습니다.
        // 또는 취소 금액을 별도의 필드로 관리할 수도 있습니다.
        // this.amount = 0L;
    }

    /**
     * 결제를 부분 취소 상태로 변경하고 남은 금액을 재계산합니다.
     * @param cancelAmount 부분 취소 금액
     */
    public void partialCancel(Long cancelAmount) {
        if (this.paymentStatus == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        if (this.amount < cancelAmount) {
            throw new IllegalArgumentException("취소 금액이 현재 결제 금액보다 큽니다.");
        }

        this.amount -= cancelAmount;
        this.paymentStatus = (this.amount == 0) ? PaymentStatus.CANCELED : PaymentStatus.PARTIAL_CANCELED;
    }
}