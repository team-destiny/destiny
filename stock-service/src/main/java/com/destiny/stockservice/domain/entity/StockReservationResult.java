package com.destiny.stockservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockReservationResult {

    RESERVED("재고 예약에 성공했습니다"),
    INVALID_REQUEST("잘못된 재고 요청입니다"),
    OUT_OF_STOCK("재고가 부족합니다."),
    ALREADY_RESERVED("이미 예약된 재고입니다.");

    private final String description;
}
