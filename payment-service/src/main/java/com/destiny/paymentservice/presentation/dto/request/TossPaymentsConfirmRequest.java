package com.destiny.paymentservice.presentation.dto.request;

import java.util.UUID;

public record TossPaymentsConfirmRequest(
    String paymentKey,
    UUID orderId,
    Integer amount
) {

}