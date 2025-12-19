package com.destiny.paymentservice.presentation.dto.request.pg.bootpay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * 부트페이 결제 취소 요청 DTO
 */
public record BootPayCancelRequest(
    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    // 우리 DB의 pgTxId 필드에 저장된 값입니다.
    String receiptId,

    @NotNull(message = "취소 금액은 필수입니다.")
    @PositiveOrZero(message = "취소 금액은 0보다 같거나 커야 합니다.")
    Integer cancelPrice,

    String cancelReason
) {
}