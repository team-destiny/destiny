package com.destiny.paymentservice.application.service.inter;

import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;

public interface PaymentService {
    // 이 서비스가 어떤 PG사를 지원하는지 반환 (TOSS, PORTONE 등)
    PaymentProvider supports();
    PaymentResponse confirmPayment(PaymentConfirmRequest request);
    PaymentResponse cancelPayment(PaymentCancelRequest request);
}