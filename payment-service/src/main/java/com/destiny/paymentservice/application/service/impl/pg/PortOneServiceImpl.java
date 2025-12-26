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
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import com.destiny.paymentservice.presentation.dto.response.pg.PortOneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortOneServiceImpl implements PaymentService {

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
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {

        // 1. 포트원 인증 헤더 생성 (V2: "PortOne {SECRET_KEY}")
        String authHeader = "PortOne " + portOneProperties.getApiSecret();

        // 2. 포트원 서버에 결제 단건 조회 호출
        PortOneResponse portOneResponse = portOneClient.getPayment(authHeader, request.pgTxId());

        // 3. 포트원 응답 상태 확인 (PAID 인지 확인)
        if (!"PAID".equals(portOneResponse.status())) {
            throw new BizException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        // 5. 이미 승인된 결제인지 확인 (멱등성 처리)
        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                throw new BizException(PaymentErrorCode.PAYMENT_ALREADY_APPROVED);
            }
        });

        // 6. 결제 엔티티 생성 또는 업데이트
        Payment payment = Payment.of(request.orderId(), request.userId(), portOneResponse.amount().total());

        payment.validatePayableStatus();
        payment.validateAmount(portOneResponse.amount().total());
        payment.completePayment(PaymentProvider.PORTONE, PaymentMethod.from(portOneResponse.method().type()), portOneResponse.id());

        return PaymentResponse.fromEntity(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {

        Payment payment = paymentRepository.findByOrderId(request.orderId())
            .orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String authHeader = "PortOne " + portOneProperties.getApiSecret();

        try {
            portOneClient.cancelPayment(
                authHeader,
                payment.getPgTxId(),
                new PortOneCancelRequest(payment.getOrderId(), payment.getPgTxId(), request.amount(), request.reason())
            );

            payment.cancel();

            return PaymentResponse.fromEntity(payment);
        } catch (Exception e) {
            log.error("취소 실패: {}", e.getMessage());
            throw e;
        }
    }
}
