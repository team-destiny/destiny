package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.feign.TossPaymentsClient;
import com.destiny.paymentservice.presentation.dto.request.pg.tosspayments.TossPaymentsCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.pg.tosspayments.TossPaymentsConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import com.destiny.paymentservice.presentation.dto.response.pg.TossPaymentsResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentsServiceImpl {

    @Value("${payment.tosspayments.secret-key}")
    private String TOSSPAYMENTS_SECRET_KEY;

    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;

    public PaymentProvider supports() {
        return PaymentProvider.TOSSPAYMENTS;
    }

    @Transactional
    public PaymentResponse confirmPayment(TossPaymentsConfirmRequest request) {
        // 토스 승인 API 호출
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((TOSSPAYMENTS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        TossPaymentsResponse response = tossPaymentsClient.confirm(authHeader, request);
        if (!"DONE".equals(response.status())) {
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        });

        Payment payment = Payment.of(request.orderId(), request.userId(), response.totalAmount());

        payment.validatePayableStatus();
        payment.validateAmount(response.totalAmount());
        payment.completePayment(PaymentProvider.TOSSPAYMENTS, PaymentMethod.from(response.method()), response.paymentKey());

        return PaymentResponse.fromEntity(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse cancelPayment(TossPaymentsCancelRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String authHeader = "Basic " + Base64.getEncoder()
            .encodeToString((TOSSPAYMENTS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        tossPaymentsClient.cancel(authHeader, payment.getPgTxId(), request);

        payment.cancel();
        return PaymentResponse.fromEntity(payment);
    }
}
