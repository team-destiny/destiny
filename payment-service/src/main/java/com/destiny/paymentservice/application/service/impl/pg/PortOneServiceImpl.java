package com.destiny.paymentservice.application.service.impl.pg;

import com.destiny.global.exception.BizException;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.domain.vo.PaymentMethod;
import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.destiny.paymentservice.domain.vo.PaymentStatus;
import com.destiny.paymentservice.infrastructure.config.PortOneProperties;
import com.destiny.paymentservice.infrastructure.feign.PortOneClient;
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
    @Transactional
    public PaymentResponse confirmPayment(PortOneConfirmRequest request) {

        // 1. 포트원 인증 헤더 생성 (V2: "PortOne {SECRET_KEY}")
        String authHeader = "PortOne " + portOneProperties.getApiSecret();

        // 2. 포트원 서버에 결제 단건 조회 호출
        PortOneResponse response = portOneClient.getPayment(authHeader, request.paymentId());

        // 3. 포트원 응답 상태 확인 (PAID 인지 확인)
        if (!"PAID".equals(response.status())) {
            log.error("포트원 결제가 완료되지 않았습니다. 상태: {}", response.status());
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        // 4. 금액 검증 (DB 주문 금액 vs 포트원 실제 결제 금액)
        // 위변조 방지를 위해 서버 측에서 반드시 금액을 비교해야 합니다.
        if (!response.amount().total().equals(request.amount())) {
            throw new BizException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 5. 이미 승인된 결제인지 확인 (멱등성 처리)
        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        });

        // 6. 결제 엔티티 생성 또는 업데이트
        Payment payment = Payment.of(request.orderId(), request.userId(), response.amount().total());

        payment.validatePayableStatus();
        payment.validateAmount(response.amount().total());
        payment.completePayment(PaymentProvider.PORTONE, PaymentMethod.from(response.method().type()), response.id());

        return PaymentResponse.fromEntity(paymentRepository.save(payment));
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
            throw e;
        }
    }
}
