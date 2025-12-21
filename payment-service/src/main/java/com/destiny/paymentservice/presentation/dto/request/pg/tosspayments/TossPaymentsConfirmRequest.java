package com.destiny.paymentservice.presentation.dto.request.pg.tosspayments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TossPaymentsConfirmRequest(
    @NotNull(message = "결제 키는 필수입니다.")
    String paymentKey,

    @NotNull(message = "주문번호는 필수입니다.")
    String orderId,

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0과 같거나 커야 합니다.")
    Integer amount
) {

}