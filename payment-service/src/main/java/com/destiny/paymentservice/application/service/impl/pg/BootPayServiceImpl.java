package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.infrastructure.config.BootPayProperties;
import com.destiny.paymentservice.infrastructure.feign.BootPayCancelPayload;
import com.destiny.paymentservice.infrastructure.feign.BootPayClient;
import com.destiny.paymentservice.infrastructure.feign.BootPayConfirmPayload;
import com.destiny.paymentservice.infrastructure.feign.BootPayTokenRequest;
import com.destiny.paymentservice.infrastructure.feign.BootPayTokenResponse;
import com.destiny.paymentservice.presentation.dto.request.pg.bootpay.BootPayCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.pg.bootpay.BootPayConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import com.destiny.paymentservice.presentation.dto.response.pg.BootPayReceiptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootPayServiceImpl {

    private final BootPayClient bootPayClient;
    private final BootPayProperties bootPayProperties;
    private final PaymentRepository paymentRepository;

    public PaymentProvider supports() {
        return PaymentProvider.BOOTPAY;
    }

    @Transactional
    public PaymentResponse confirmPayment(BootPayConfirmRequest request) {

        // 부트페이 토큰 및 승인 요청
        BootPayReceiptResponse response = bootPayClient.confirmPayment(
            "Bearer " + getAccessToken(),
            "application/json",
            new BootPayConfirmPayload(request.receiptId(), bootPayProperties.getRestApiKey())
        );

        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseGet(() -> {
                return Payment.of(request.orderId(), request.userId(), response.price());
            });

        // 3. 금액 검증 및 완료 처리
        payment.validatePayableStatus();
        payment.validateAmount(response.price());
        payment.completePayment(PaymentProvider.BOOTPAY, PaymentMethod.from(response.methodSymbol()), response.receiptId());

        return PaymentResponse.fromEntity(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse cancelPayment(BootPayCancelRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        bootPayClient.cancelPayment(
            "Bearer " + getAccessToken(),
            new BootPayCancelPayload(payment.getPgTxId(), request.cancelPrice(), request.cancelReason())
        );

        payment.cancel();
        return PaymentResponse.fromEntity(paymentRepository.save(payment));
    }

    private String getAccessToken() {
        BootPayTokenResponse response = bootPayClient.getAccessToken(new BootPayTokenRequest(
            bootPayProperties.getRestApiKey(),
            bootPayProperties.getPrivateKey()
        ));
        return response.access_token();
    }
}