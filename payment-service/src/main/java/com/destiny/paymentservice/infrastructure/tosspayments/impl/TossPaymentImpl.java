package com.destiny.paymentservice.infrastructure.tosspayments.impl;

import com.destiny.paymentservice.domain.dto.PgRequestDto;
import com.destiny.paymentservice.domain.dto.PgResponseDto;
import com.destiny.paymentservice.domain.port.PaymentGateway;
import com.destiny.paymentservice.domain.vo.PaymentType;
import com.destiny.paymentservice.infrastructure.tosspayments.client.TossPaymentsClient;
import com.destiny.paymentservice.infrastructure.tosspayments.dto.request.TossConfirmRequest;
import com.destiny.paymentservice.infrastructure.tosspayments.dto.response.TossConfirmResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TossPaymentImpl implements PaymentGateway {

    // Feign Client 주입 (실제 HTTP 통신은 Feign이 위임받아 처리)
    private final TossPaymentsClient tossPaymentsClient;

    @Override
    public PgResponseDto authorize(PgRequestDto request) {

        // 1. 도메인 요청 DTO를 TOSS 전용 요청 DTO로 변환
        TossConfirmRequest tossRequest = new TossConfirmRequest(
            request.paymentKey(),
            request.orderId(),
            request.totalAmount()
        );

        try {
            // 2. Feign Client를 사용하여 TOSS API 호출 (HTTP 통신 코드가 사라짐)
            TossConfirmResponse response = tossPaymentsClient.confirmPayment(tossRequest);

            // 3. TOSS 응답을 도메인 표준 PgResponseDto로 변환
            if (response.isSuccess()) {
                return new PgResponseDto(
                    true,
                    response.paymentKey(),
                    response.orderId(),
                    response.totalAmount(),
                    PaymentType.TOSSPAYMENTS,
                    null,
                    null
                );
            } else {
                // TOSS API는 실패 시에도 200 OK를 반환할 수 있으므로, 상태 코드로 체크
                // NOT_FOUND_PAYMENT_SESSION과 같은 에러는 보통 400 Bad Request로 FeignException 처리됨.
                // 하지만 혹시 모를 내부 실패(200 OK + "status":"CANCELED" 등) 대비
                return new PgResponseDto(
                    false,
                    null,
                    request.orderId(),
                    request.totalAmount(),
                    PaymentType.TOSSPAYMENTS,
                    response.code(),
                    response.message()
                );
            }

        } catch (FeignException e) {
            // 4. HTTP 통신 오류(4xx, 5xx) 발생 시 처리 (NOT_FOUND_PAYMENT_SESSION 등)
            // FeignException.responseBody()를 사용해 실패 JSON을 파싱하여 에러 메시지를 얻을 수 있으나,
            // 여기서는 단순화하여 처리합니다.
            return new PgResponseDto(
                false,
                null,
                request.orderId(),
                request.totalAmount(),
                PaymentType.TOSSPAYMENTS,
                String.valueOf(e.status()), // HTTP 상태 코드를 에러 코드로 사용
                "TossPayments 통신 오류: " + e.getMessage()
            );
        }
    }

    @Override
    public PgResponseDto cancel(String pgTxId, Long cancelAmount, String reason) {
        // TODO: Feign Client를 사용하여 취소 API 호출
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public PgResponseDto inquire(String pgTxId) {
        // TODO: Feign Client를 사용하여 조회 API 호출
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}