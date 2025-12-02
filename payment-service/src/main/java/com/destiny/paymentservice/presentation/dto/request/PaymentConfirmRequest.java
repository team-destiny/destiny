package com.destiny.paymentservice.presentation.dto.request;

public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}