package com.destiny.sagaorchestrator.domain.entity;

public enum SagaStatus {
    CREATED,        // 주문 서비스 -> 오케스트레이션 프로세스 시작 시점
    PROGRESS,       // 유저/재고/결제 등 처리 흐름 진행 중
    COMPLETED,      // 사가 성공
    FAILED,         // 중간 단계에서 실패함
    COMPENSATED     // 실패 후 보상(롤백)까지 완료됨
}
