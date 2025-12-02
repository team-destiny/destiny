package com.destiny.paymentservice.infrastructure.tosspayments.dto.request;

public record TossConfirmRequest(
    String paymentKey, // 결제 위젯을 누르면 생성되는 필수 값
    String orderId,
    Long amount
) {

}