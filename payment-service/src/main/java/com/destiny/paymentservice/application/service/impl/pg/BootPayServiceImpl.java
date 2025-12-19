package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.config.BootPayProperties;
import com.destiny.paymentservice.infrastructure.feign.BootPayClient;
import com.destiny.paymentservice.infrastructure.feign.BootPayConfirmPayload;
import com.destiny.paymentservice.infrastructure.feign.BootPayTokenRequest;
import com.destiny.paymentservice.infrastructure.feign.BootPayTokenResponse;
import com.destiny.paymentservice.presentation.dto.request.pg.bootpay.BootPayConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import com.destiny.paymentservice.presentation.dto.response.pg.BootPayReceiptResponse;
import java.util.HashMap;
import java.util.Map;
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

    public PaymentProvider getProvider() {
        return PaymentProvider.BOOTPAY;
    }

    @Transactional
    public PaymentResponse confirmPayment(BootPayConfirmRequest request) {
        String accessToken = getAccessToken();

        BootPayReceiptResponse response = bootPayClient.confirmPayment(
            "Bearer " + accessToken,
            "application/json",
            new BootPayConfirmPayload(request.receiptId(), bootPayProperties.getRestApiKey())
        );

        // 2. DB 주문 조회 (없으면 정적 팩토리 메서드 'of'를 사용하여 PENDING 상태로 생성)
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseGet(() -> {
                log.info("결제 내역이 없어 객체를 새로 생성합니다. orderId: {}", request.orderId());
                return Payment.of(request.orderId(), request.userId(), response.price());
            });

        // 3. 금액 검증
        if (!payment.getAmount().equals(response.price())) {
            throw new BizException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 4. 결제 정보 업데이트 (결제 완료 처리)
        log.info("부트페이 승인 응답 상세: {}", response); // response 전체 구조 확인
        payment.paid(PaymentProvider.BOOTPAY, PaymentMethod.from(response.methodSymbol()));
        payment.assignPgTxId(response.receiptId());

        // 5. 최종 결과 저장
        paymentRepository.save(payment);

        return PaymentResponse.fromEntity(payment);
    }

    private String getAccessToken() {
        log.info("Token Request - RestKey: {}, PrivateKey: {}",
            bootPayProperties.getRestApiKey(), bootPayProperties.getPrivateKey());

        BootPayTokenResponse response = bootPayClient.getAccessToken(new BootPayTokenRequest(
            bootPayProperties.getRestApiKey(),
            bootPayProperties.getPrivateKey()
        ));

        return response.access_token();
    }
}