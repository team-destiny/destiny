package com.destiny.paymentservice.presentation.dto.request;

import com.destiny.paymentservice.domain.vo.PaymentProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * 결제 생성 요청 DTO (PENDING 상태 생성)
 */
public record PaymentRequest(
    @NotNull(message = "주문번호는 필수입니다.")
    UUID orderId,

    PaymentProvider paymentType,

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0과 같거나 커야 합니다.")
    Integer amount
) {

}