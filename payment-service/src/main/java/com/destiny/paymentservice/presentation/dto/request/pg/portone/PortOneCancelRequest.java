package com.destiny.paymentservice.presentation.dto.request.pg.portone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * 포트원 결제 취소 요청 DTO (TossPaymentsCancelRequest 형식 참고)
 */
public record PortOneCancelRequest(
    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    /**
     * 포트원의 결제 고유 ID (paymentId)
     * 우리 DB의 pgTxId 필드에 저장된 값입니다.
     */
    @NotBlank(message = "포트원 결제 ID(paymentId)는 필수입니다.")
    String paymentId,

    @NotNull(message = "취소 금액은 필수입니다.")
    @PositiveOrZero(message = "취소 금액은 0과 같거나 커야 합니다.")
    Integer amount,

    String reason

) {
}