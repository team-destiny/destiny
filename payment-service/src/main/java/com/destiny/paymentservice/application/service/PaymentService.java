package com.destiny.paymentservice.application.service;

import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.UUID;

public interface PaymentService {
    // ⭐️ 모두 PaymentResponse 반환
    PaymentResponse requestPayment(PaymentRequest request);
    PaymentResponse confirmPayment(PaymentConfirmRequest request);
    PaymentResponse cancelPayment(PaymentCancelRequest request);
    PaymentResponse getPaymentByOrderId(UUID orderId);
}