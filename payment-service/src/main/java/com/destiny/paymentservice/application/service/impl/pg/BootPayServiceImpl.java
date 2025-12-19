package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
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

    @Transactional
    public PaymentResponse cancelPayment(BootPayCancelRequest request) {
        // 1. DB에서 주문 번호로 결제 내역 조회
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 부트페이 액세스 토큰 가져오기
        String accessToken = getAccessToken();

        try {
            BootPayReceiptResponse response = bootPayClient.cancelPayment(
                "Bearer " + accessToken,
                new BootPayCancelPayload(payment.getPgTxId(), request.cancelPrice(), request.cancelReason())
            );

            // 3. 결제 엔티티 상태 변경 (PAID -> CANCELED)
            payment.cancel();
            paymentRepository.save(payment);

            log.info("부트페이 결제 취소 완료: orderId={}, receiptId={}",
                request.orderId(), payment.getPgTxId());

            return PaymentResponse.fromEntity(payment);
        } catch (Exception e) {
            log.error("부트페이 취소 요청 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    private String getAccessToken() {
        BootPayTokenResponse response = bootPayClient.getAccessToken(new BootPayTokenRequest(
            bootPayProperties.getRestApiKey(),
            bootPayProperties.getPrivateKey()
        ));

        return response.access_token();
    }
}