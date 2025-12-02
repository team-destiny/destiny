package com.destiny.paymentservice.domain.port;

import com.destiny.paymentservice.domain.dto.PgRequestDto;
import com.destiny.paymentservice.domain.dto.PgResponseDto;

public interface PaymentGateway {

    /**
     * PG사에 최종 결제 승인을 요청합니다.
     * @param request 공통 결제 요청 정보 (orderId, amount 등)
     * @return PgResponseDto 공통 결제 응답 정보
     */
    PgResponseDto authorize(PgRequestDto request);

    /**
     * 승인된 결제를 취소합니다.
     * @param pgTxId PG사 거래 고유 ID
     * @param cancelAmount 취소 금액
     * @param reason 취소 사유
     * @return PgResponseDto 공통 결제 응답 정보
     */
    PgResponseDto cancel(String pgTxId, Long cancelAmount, String reason);

    /**
     * 특정 거래의 현재 상태를 PG사에 문의합니다.
     * @param pgTxId PG사 거래 고유 ID
     * @return PgResponseDto 공통 결제 응답 정보
     */
    PgResponseDto inquire(String pgTxId);

    // 기타 필요한 기능: 환불, 정기 결제 빌링 키 발급 등

}