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
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;

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

        PgResponseDto response;

        try {
            // 1. PG사에 최종 결제 승인 요청 (PG사 종속 로직은 Gateway 내부에서 처리)
            response = paymentGateway.authorize(request);
        } catch (Exception e) {
            // ✨ [수정] PG 연동 중 네트워크/통신 예외 발생 시 처리
            // 해당 예외는 트랜잭션 롤백을 유도하고, 표준 실패 응답을 반환합니다.
            // TODO: 로깅 및 모니터링 추가
            // PgResponseDto.createFailureResponse() 메서드는 아래 2번 항목을 참고하여 PgResponseDto에 추가해야 합니다.
            return PgResponseDto.failResponse(
                request.orderId(),
                "PG_COMM_ERR", // PG 통신 오류를 의미하는 표준 에러 코드
                "PG 연동 중 오류가 발생했습니다: " + e.getMessage()
            );
        }

        if (response.isSuccess()) {
            // 2. 성공 시 DB에 결제 기록 저장
            Payment payment = Payment.createSuccess(
                response.orderId(),
                response.pgTxId(),
                response.pgType(),
                response.finalAmount()
            );

            paymentRepository.save(payment);

            // TODO: 주문 서비스에 결제 완료 알림 (Kafka)
        } else {
            // 3. 실패 시 로직 처리 (PG사에서 비즈니스 실패 응답을 받은 경우)
            // 별도의 로깅만 하고, PG사에서 받은 응답(response)을 그대로 Controller로 반환
        }

        return response;
    }

    // TODO: 취소 로직 (cancelPayment), 환불 로직 등 추가 구현 필요
}