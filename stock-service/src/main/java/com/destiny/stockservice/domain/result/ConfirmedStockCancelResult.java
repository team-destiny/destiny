package com.destiny.stockservice.domain.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfirmedStockCancelResult {

    CANCEL_SUCCEEDED("재고 취소 요청을 성공했습니다."),
    INVALID_REQUEST("잘못된 재고 취소 요청값입니다."),
    NO_RESERVATION("취소할 재고 확정 건이 없습니다."),
    CANCEL_FAILED("재고 취소 요청에 실패했습니다.");

    private final String description;
}
