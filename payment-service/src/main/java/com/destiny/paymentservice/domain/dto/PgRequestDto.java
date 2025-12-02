package com.destiny.paymentservice.domain.dto;

import com.destiny.paymentservice.domain.vo.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PgRequestDto(
    @NotBlank(message = "주문 번호는 필수 항목입니다.")
    String orderId,         // 결제할 주문 번호

    @NotNull(message = "결제 금액은 필수입니다.")
    @PositiveOrZero(message = "결제 금액은 0과 같거나 커야 합니다.")
    Long totalAmount,       // 결제 금액

    @NotNull(message = "PG사 유형은 필수입니다.")
    PaymentType paymentType,     // 요청할 PG사 유형

    String paymentKey, // 토스페이먼츠 필수 키

    String customerEmail,   // 고객 이메일
    String customerName,    // 고객 이름
    String customerMobilePhone // 고객 전화번호
) {

}