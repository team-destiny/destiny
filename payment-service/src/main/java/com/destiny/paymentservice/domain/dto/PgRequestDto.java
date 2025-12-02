package com.destiny.paymentservice.domain.dto;

import com.destiny.paymentservice.domain.vo.PaymentType;

public record PgRequestDto(
    String orderId,         // 결제할 주문 번호
    Long totalAmount,            // 결제 금액
    PaymentType paymentType,     // 요청할 PG사 유형
    String paymentKey,
    String customerEmail,   // 고객 이메일
    String customerName,    // 고객 이름
    String customerMobilePhone // 고객 전화번호
) {

}