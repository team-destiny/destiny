package com.destiny.paymentservice.application.service.inter;

import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentProvider supports();
    PaymentResponse confirmPayment(PaymentConfirmRequest request);
    PaymentResponse cancelPayment(PaymentCancelRequest request);
}