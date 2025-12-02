package com.destiny.paymentservice.domain.vo;

public enum PaymentStatus {
    // ---- 결제 초기 상태 ----
    PENDING,          // 결제 승인 대기 (위젯/결제창 띄운 직후)

    // ---- 성공 및 진행 상태 ----
    PAID,           // 결제 승인 완료 (DB에 최종 저장된 상태)
    WAITING_FOR_DEPOSIT, // 가상계좌 발급 후 입금 대기

    // ---- 실패 및 취소 상태 ----
    CANCELED,       // 결제가 취소됨 (전액 또는 부분 취소 포함)
    FAILED,         // 결제 승인 과정에서 오류 발생 (카드 거절 등)
    PARTIAL_CANCELED // 부분 취소가 발생한 상태
}