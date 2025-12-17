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
import java.util.UUID;
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

    /**
     * 결제 승인 (PENDING → PAID)
     */
    @Transactional
    public PaymentResponse confirmPayment(TossPaymentsConfirmRequest request) {

        // 1️⃣ [검증] 토스 API 호출 전, 우리 DB에 해당 주문이 있는지 확인 (위변조 방지)
        // 만약 Ready 단계에서 미리 저장했다면 조회를, 아니라면 여기서 생성 준비를 합니다.
        // 여기서는 일단 '승인 시점에 저장'하시는 흐름에 맞춰 userId만 매칭하겠습니다.

        // 2️⃣ Toss 결제 승인 API 호출을 위한 헤더 생성
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((TOSSPAYMENTS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        // 3️⃣ 토스 승인 API 호출
        TossPaymentsResponse toss = tossPaymentsClient.confirm(authHeader, request);
        log.info("Toss confirm response status = {}", toss.status());

        // 4️⃣ 응답 상태 확인
        if (!"DONE".equals(toss.status())) {
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        // 5️⃣ 중복 결제 확인 로직 (이미 같은 orderId가 PAID 상태인지)
        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        });

        // 6️⃣ 결제 엔티티 생성 (프런트에서 넘겨준 userId 사용)
        Payment payment = Payment.of(request.orderId(), request.userId(), toss.totalAmount());

        // 7️⃣ 결제 완료 정보 업데이트
        payment.paid(PaymentProvider.TOSSPAYMENTS, PaymentMethod.from(toss.method()));
        payment.assignPgTxId(toss.paymentKey());

        paymentRepository.save(payment);

        return PaymentResponse.fromEntity(payment);
    }


    /**
     * 결제 취소 (PAID → CANCELED)
     */
    @Transactional
    public PaymentResponse cancelPayment(TossPaymentsCancelRequest request) {

        // 1. 우리 DB에서 주문 번호로 결제 내역 조회
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 토스 인증 헤더 생성
        String authHeader = "Basic " + Base64.getEncoder()
            .encodeToString((TOSSPAYMENTS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        // 3. 토스 취소 API 호출 (payment.getPgTxId()가 바로 paymentKey입니다)
        // DTO(request)에는 취소 사유(reason)와 금액(amount)이 담겨 있습니다.
        tossPaymentsClient.cancel(authHeader, payment.getPgTxId(), request);

        // 4. 우리 DB 상태 변경 (PAID -> CANCELED)
        payment.cancel();

        return PaymentResponse.fromEntity(payment);
    }
}
