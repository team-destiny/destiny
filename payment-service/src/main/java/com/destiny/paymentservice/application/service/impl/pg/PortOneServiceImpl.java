package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.paymentservice.application.service.inter.PaymentService;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class PortOneServiceImpl implements PaymentService {

    @Override
    public PaymentProvider supports() {
        return PaymentProvider.PORTONE;
    }

    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        return null;
    }

    @Override
    public PaymentResponse cancelPayment(PaymentCancelRequest request,
        CustomUserDetails userDetails) {
        return null;
    }
}
