package com.destiny.paymentservice.application.service;

import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponse requestPayment(PaymentRequest request, CustomUserDetails userDetails);
    PaymentResponse confirmPayment(PaymentConfirmRequest request, CustomUserDetails userDetails);
    PaymentResponse cancelPayment(PaymentCancelRequest request, CustomUserDetails userDetails);
    PaymentResponse getPaymentByOrderId(UUID orderId, CustomUserDetails userDetails);
    PageResponseDto<PaymentResponse> getAllPayments(Pageable pageable, CustomUserDetails userDetails);
}