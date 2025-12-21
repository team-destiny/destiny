package com.destiny.paymentservice.presentation.dto.request;

import com.destiny.paymentservice.domain.vo.PaymentProvider;
import java.util.UUID;


public record PaymentCancelRequest(
    UUID orderId,
    UUID userId,
    Integer amount,
    String reason,
    PaymentProvider provider
) {

}