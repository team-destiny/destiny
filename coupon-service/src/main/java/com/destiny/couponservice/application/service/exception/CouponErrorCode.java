package com.destiny.couponservice.application.service.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Generated;
import org.springframework.http.HttpStatus;

public enum CouponErrorCode implements ResponseCode {
    DUPLICATE_TEMPLATE_CODE(HttpStatus.CONFLICT, "COUPON-001", "이미 존재하는 쿠폰 코드입니다."),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON-002", "쿠폰 템플릿을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Generated
    public HttpStatus getStatus() {
        return this.status;
    }

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    CouponErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
