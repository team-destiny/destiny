package com.destiny.paymentservice.application.service;

import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponse requestPayment(PaymentRequest request);
    PaymentResponse confirmPayment(PaymentConfirmRequest request);
    PaymentResponse cancelPayment(PaymentCancelRequest request);
    PaymentResponse getPaymentByOrderId(UUID orderId);
    PageResponseDto<PaymentResponse> getAllPayments(Pageable pageable);
}