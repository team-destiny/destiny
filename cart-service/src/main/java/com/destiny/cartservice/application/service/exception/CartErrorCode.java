package com.destiny.cartservice.application.service.exception;

import com.destiny.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements ResponseCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART-001", "장바구니 항목을 찾을 수 없습니다."),
    INVALID_OWNER(HttpStatus.FORBIDDEN, "CART-002", "본인의 장바구니가 아닙니다."),
    INVALID_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "CART-003", "삭제 요청 값이 잘못되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
