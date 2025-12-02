package com.destiny.paymentservice.domain.dto;

import com.destiny.paymentservice.domain.vo.PaymentType;

public record PgResponseDto(
    boolean isSuccess,      // 결제 성공 여부
    String pgTxId,          // PG사 거래 고유 ID (결제 성공 시 필수)
    String orderId,         // 결제 완료된 주문 번호
    Long finalAmount,       // 최종 승인된 금액
    PaymentType pgType,     // 사용된 PG사 유형
    String failCode,        // 실패 시 에러 코드
    String failMessage      // 실패 시 에러 메시지

) {

}