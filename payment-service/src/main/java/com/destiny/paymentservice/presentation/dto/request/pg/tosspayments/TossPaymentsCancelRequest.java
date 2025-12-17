package com.destiny.paymentservice.presentation.dto.request.pg.tosspayments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * Toss 결제 취소 요청 DTO
 */
public record TossPaymentsCancelRequest(

    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    // 토스 API 호출 시 경로 변수(PathVariable)로 사용할 수 있도록 추가합니다.
    // 우리 DB의 pgTxId 필드에 저장된 값입니다.
    @NotBlank(message = "결제 키(paymentKey)는 필수입니다.")
    String paymentKey,

    @NotNull(message = "취소 금액은 필수입니다.")
    @PositiveOrZero(message = "취소 금액은 0보다 같거나 커야 합니다.")
    Integer cancelAmount, // 토스 API 필드명에 맞춰 cancelAmount로 명시하면 더 명확합니다.

    String cancelReason
) {
}