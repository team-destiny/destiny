package com.destiny.stockservice.domain.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockReservationCancelResult {
    CANCEL_SUCCEEDED("재고 예약 취소가 정상적으로 처리되었습니다."),
    INVALID_REQUEST("잘못된 요청값으로 인한 재고 취소 실패입니다."),
    NO_RESERVATION("취소할 재고 예약 건이 없습니다."),
    INVALID_REQUEST("잘못된 재고 취소 요청값입니다."),
    ALREADY_CANCELED("이미 취소된 재고 예약 건입니다.");

    private final String description;
}
