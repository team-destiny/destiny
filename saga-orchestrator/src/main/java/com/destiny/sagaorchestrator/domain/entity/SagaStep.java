package com.destiny.sagaorchestrator.domain.entity;

public enum SagaStep {
    ORDER_CREATED,          // 주문 서비스에서 시작 이벤트 발행됨
    USER_VALIDATION,        // 유저 검증 단계
    USER_VALIDATED,         // 유저 검증 성공
    PRODUCT_VALIDATION,     // 상품 검증 단계
    PRODUCT_VALIDATED,      // 상품 검증 성공
    STOCK_RESERVATION,      // 재고 차감(예약)
    STOCK_RESERVED,         // 재고 성공
    PAYMENT_APPROVAL,       // 결제 승인 요청
    PAYMENT_APPROVED,       // 결제 성공
    ORDER_COMPLETED,        // 사가 완료 (최종)
    ORDER_COMPENSATION,     // 보상 트랜잭션 수행
    ORDER_COMPENSATED       // 보상 완료
}
