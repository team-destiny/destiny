package com.destiny.paymentservice.presentation.dto.request.pg.tosspayments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Toss 결제 취소 요청 DTO
 */
public record TossPaymentsCancelRequest(

    @NotNull(message = "취소 금액은 필수입니다.")
    @PositiveOrZero(message = "취소 금액은 0보다 같거나 커야 합니다.")
    Integer cancelAmount,

    String cancelReason
) {
}