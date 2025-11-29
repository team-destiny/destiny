package com.destiny.sagaorchestrator.infrastructure.messaging.event.outcome;

import java.util.UUID;

public record OrderCreateFailedEvent(
    UUID sagaId,
    UUID orderId,

    /*
     * failReason
     * - 사람이 직접 이해할 수 있는 실패 사유 메시지
     * - ex) "사용자 검증 실패: 존재하지 않는 사용자입니다"
     * - ex) "상품 재고 부족"
     * - ex) "결제 승인 거절"
     *
     * 언제 사용?
     * - 디버깅, 로그 기록, 슬랙 알림, 관리자 모니터링 화면에서 그대로 출력
     * - 사람 중심 메시지이므로 개발자/운영자가 확인하기 위한 용도
     */
    String failReason,

    /*
     * errorCode
     * - 시스템이 식별하기 위한 고정된 에러 코드 값
     * - ex) "USER_NOT_FOUND", "STOCK_OUT_OF_RANGE", "PAYMENT_DECLINED"
     *
     * 언제 사용?
     * - 오케스트레이터가 실패 타입에 따라 다음 보상 로직을 선택할 때
     * - 조건문/스위치문에서 로직 분기할 때
     * - 상태머신(Statemachine)이나 모니터링 파이프라인에서 분석할 때
     *
     * ※ failReason은 사람이 읽는 용도,
     *   errorCode는 시스템/로직이 판단하는 용도.
     */
    String errorCode,

    /*
     * failedService
     * - 어느 서비스에서 실패가 발생했는지 표시하는 값
     * - ex) "USER-SERVICE", "STOCK-SERVICE", "PAYMENT-SERVICE"
     *
     * 언제 사용?
     * - 오케스트레이터가 "어떤 단계에서 실패가 발생했는지" 파악하기 위한 핵심값
     * - SAGA의 '보상 트랜잭션(compensation)' 실행 시:
     *      USER 단계 실패 → 주문 롤백만 수행
     *      STOCK 단계 실패 → 재고 복원(보상) + 주문 취소
     *      PAYMENT 단계 실패 → 재고 보상 + 주문 취소
     *
     * 즉, 실패한 서비스에 따라 보상 로직이 달라지기 때문에 반드시 필요.
     */
    String failedService
) {

}
