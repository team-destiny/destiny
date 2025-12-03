package com.destiny.orderservice.infrastructure.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderError implements ResponseCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER-001", "해당 주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER-002", "해당 주문 아이템을 찾을 수 없습니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.CONFLICT, "ORDER-003", "일부 상품이 이미 처리중이어서 전체 주문을 취소할 수 없습니다.")



    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    OrderError(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
