package com.destiny.paymentservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * 결제 전액 취소 요청 DTO (PAID -> CANCELED)
 */
public record PaymentCancelRequest(
    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    @NotNull(message = "취소 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0보다 같거나 커야 합니다.")
    Integer amount,

    String reason
) {

}