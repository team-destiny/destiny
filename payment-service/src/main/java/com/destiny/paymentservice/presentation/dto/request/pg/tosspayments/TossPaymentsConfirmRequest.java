package com.destiny.paymentservice.presentation.dto.request.pg.tosspayments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record TossPaymentsConfirmRequest(
    @NotNull(message = "결제 키는 필수입니다.")
    String paymentKey,

    UUID userId,

    @NotNull(message = "주문번호는 필수입니다.")
    UUID orderId,

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0과 같거나 커야 합니다.")
    Integer amount
) {

}