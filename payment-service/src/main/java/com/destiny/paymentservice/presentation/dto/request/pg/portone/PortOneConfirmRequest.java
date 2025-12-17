package com.destiny.paymentservice.presentation.dto.request.pg.portone;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record PortOneConfirmRequest(
    @NotNull(message = "포트원 결제 ID는 필수입니다.")
    String paymentId, // 포트원의 고유 거래 식별자

    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0보다 커야 합니다.")
    Integer amount
) {
}