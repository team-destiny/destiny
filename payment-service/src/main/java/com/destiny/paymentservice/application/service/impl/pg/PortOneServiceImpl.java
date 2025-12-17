package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.application.service.inter.PaymentService;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.config.PortOneProperties;
import com.destiny.paymentservice.infrastructure.feign.PortOneClient;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.request.pg.portone.PortOneCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.pg.portone.PortOneConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import com.destiny.paymentservice.presentation.dto.response.pg.PortOneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortOneServiceImpl {

    private final PortOneClient portOneClient;
    private final PaymentRepository paymentRepository;
    private final PortOneProperties portOneProperties;

    public PaymentProvider supports() {
        return PaymentProvider.PORTONE;
    }

    /**
     * 포트원 결제 승인 및 검증 (V2)
     */
    public PaymentResponse confirmPayment(PortOneConfirmRequest request) {

        // 1. 포트원 인증 헤더 생성 (V2: "PortOne {SECRET_KEY}")
        String authHeader = "PortOne " + portOneProperties.getApiSecret();

        // 2. 포트원 서버에 결제 단건 조회 호출
        PortOneResponse portOne = portOneClient.getPayment(authHeader, request.paymentId());

        // 3. 포트원 응답 상태 확인 (PAID 인지 확인)
        if (!"PAID".equals(portOne.status())) {
            log.error("포트원 결제가 완료되지 않았습니다. 상태: {}", portOne.status());
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        // 4. 금액 검증 (DB 주문 금액 vs 포트원 실제 결제 금액)
        // 위변조 방지를 위해 서버 측에서 반드시 금액을 비교해야 합니다.
        if (!portOne.amount().total().equals(request.amount())) {
            log.error("결제 금액이 일치하지 않습니다. 요청: {}, 실제: {}", request.amount(), portOne.amount().total());
            throw new BizException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 5. 이미 승인된 결제인지 확인 (멱등성 처리)
        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        });

        // 6. 결제 엔티티 생성 또는 업데이트
        // (기존에 PENDING 데이터가 없다면 새로 생성, 있다면 조회 후 업데이트)
        Payment payment = Payment.of(request.orderId(), request.userId(), portOne.amount().total());

        // 7. 결제 완료 정보 저장
        // PortOneResponse에서 받은 결제 수단 타입을 우리 시스템의 Value Object로 변환하여 저장
        payment.paid(PaymentProvider.PORTONE, PaymentMethod.from(portOne.method().type()));
        payment.assignPgTxId(portOne.id()); // pgTxId에 포트원의 paymentId 저장

        paymentRepository.save(payment);

        log.info("포트원 결제 검증 및 저장 완료: {}", payment.getPgTxId());

        return PaymentResponse.fromEntity(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(PortOneCancelRequest request) {

        // 1. DB 조회 및 검증
        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 인증 헤더
        String authHeader = "PortOne " + portOneProperties.getApiSecret();

        try {
            // 3. DTO를 그대로 던짐!
            // @JsonProperty 덕분에 포트원 서버에는 필요한 필드만 바뀐 이름으로 전달됩니다.
            PortOneResponse response = portOneClient.cancelPayment(
                authHeader,
                request.paymentId(),
                request
            );

            payment.cancel();
            paymentRepository.save(payment);

            return PaymentResponse.fromEntity(payment);
        } catch (Exception e) {
            log.error("취소 실패: {}", e.getMessage());
            throw new BizException(PaymentErrorCode.PAYMENT_INVALID_REQUEST);
        }
    }
}
