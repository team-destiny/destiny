package com.destiny.paymentservice.application.service.inter;

import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface PaymentQueryService {
    PaymentResponse getPaymentByOrderId(UUID orderId, CustomUserDetails userDetails);
    PageResponseDto<PaymentResponse> getAllPayments(Pageable pageable, CustomUserDetails userDetails);
}