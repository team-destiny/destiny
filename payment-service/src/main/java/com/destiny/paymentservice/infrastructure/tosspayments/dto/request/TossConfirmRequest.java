package com.destiny.paymentservice.infrastructure.tosspayments.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TossConfirmRequest(
    @NotBlank(message = "결제 키는 필수입니다.")
    String paymentKey, // 결제 위젯을 누르면 생성되는 필수 값

    @NotBlank(message = "주문 번호는 필수입니다.")
    String orderId,

    @NotNull(message = "금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0과 같거나 커야 합니다.")
    Long amount
) {

}