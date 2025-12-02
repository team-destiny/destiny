package com.destiny.paymentservice.application.service;

import com.destiny.paymentservice.domain.dto.PgRequestDto;
import com.destiny.paymentservice.domain.dto.PgResponseDto;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.port.PaymentGateway;
import com.destiny.paymentservice.domain.port.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCommandService {

    // [주입] PaymentGateway 인터페이스 타입으로 Toss 구현체 주입 (현재는 TOSS만 가정)
    // Spring 환경에서 @Qualifier 등을 사용해 특정 구현체를 주입받아야 합니다.
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;

    // 현재는 TOSS 구현체를 명시적으로 주입받는다고 가정
    public PaymentCommandService(PaymentGateway tossPaymentImpl, PaymentRepository paymentRepository) {
        this.paymentGateway = tossPaymentImpl;
        this.paymentRepository = paymentRepository;
    }

    /**
     * 최종 결제 승인을 요청하고, 성공 시 DB에 결제 기록을 저장합니다.
     * @param request 클라이언트에서 넘어온 결제 요청 DTO
     * @return PgResponseDto PG사 응답 결과
     */
    @Transactional // 트랜잭션 관리
    public PgResponseDto confirmAndSavePayment(PgRequestDto request) {

        // 1. PG사에 최종 결제 승인 요청 (PG사 종속 로직은 Gateway 내부에서 처리)
        // PG사 독립적인 PgRequestDto를 전달합니다.
        PgResponseDto response = paymentGateway.authorize(request);

            // 2. 성공 시 DB에 결제 기록 저장
            Payment payment = Payment.createSuccess(
                response.orderId(),
                response.pgTxId(),
                response.pgType(),
                response.finalAmount()
            );

            paymentRepository.save(payment);

            // TODO: 주문 서비스에 결제 완료 알림 (Kafka)
        return response;
    }

    // TODO: 취소 로직 (cancelPayment), 환불 로직 등 추가 구현 필요
}