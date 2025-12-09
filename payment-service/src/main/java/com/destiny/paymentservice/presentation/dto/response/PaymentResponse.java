package com.destiny.paymentservice.presentation.dto.response;

import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import java.util.UUID;

/**
 * 결제 정보 응답 DTO
 */
public record PaymentResponse(
    UUID id,
    UUID orderId,
    UUID userId,
    PaymentProvider paymentProvider,
    PaymentMethod paymentMethod,
    Integer amount,
    PaymentStatus paymentStatus
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getPaymentProvider(),
            payment.getPaymentMethod(),
            payment.getAmount(),
            payment.getPaymentStatus()
        );
    }
}