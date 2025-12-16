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
    INVALID_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "CART-003", "삭제 요청 값이 잘못되었습니다."),
    INVALID_CLEAR_EVENT(HttpStatus.BAD_REQUEST, "CART-004", "장바구니 비우기 요청이 잘못되었습니다."),
    NOT_FOUND_PRODUCT_DATA(HttpStatus.NOT_FOUND, "CART-005", "상품 정보를 찾을 수 없습니다."),
    PRODUCT_SERVICE_UNAVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, "CART-006", "일시적인 오류로 상품 서비스를 이용할 수 없습니다."),
    CANNOT_ADD_UNAVAILABLE_PRODUCT(HttpStatus.BAD_REQUEST, "CART-007", "해당 상품은 품절되었거나 숨겨진 상태로 장바구니에 담을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
