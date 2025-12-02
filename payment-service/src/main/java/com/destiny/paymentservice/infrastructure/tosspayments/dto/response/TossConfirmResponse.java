package com.destiny.paymentservice.infrastructure.tosspayments.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // TOSS 응답 필드가 너무 많으므로 필요한 것만 받습니다.
public record TossConfirmResponse(
    // 성공 시 필수 필드
    String paymentKey,
    String orderId,
    String status,
    Long totalAmount,

    // 실패 시 필수 필드 (응답 JSON에 포함되어 반환될 수 있음)
    String code,
    String message
) {
    // 결제 성공 여부를 판단하는 헬퍼 메서드
    public boolean isSuccess() {
        return "DONE".equals(status);
    }
}
