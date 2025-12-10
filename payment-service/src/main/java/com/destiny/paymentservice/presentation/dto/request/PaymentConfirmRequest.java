package com.destiny.paymentservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * 결제 승인 요청 DTO (PENDING -> PAID)
 * 실제 PG사 연동에서는 pgTxId(PG사 거래ID), amount 등 PG사에서 반환한 정보가 추가됩니다.
 */
public record PaymentConfirmRequest(
    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0보다 같거나 커야 합니다.")
    Integer amount
) {

}