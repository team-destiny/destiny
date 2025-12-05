package com.destiny.paymentservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 결제 전액 취소 요청 DTO (PAID -> CANCELED)
 */
public record PaymentCancelRequest(
    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    String reason
) {

}