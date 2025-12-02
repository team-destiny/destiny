package com.destiny.couponservice.application.service.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Generated;
import org.springframework.http.HttpStatus;

public enum IssuedCouponErrorCode implements ResponseCode {

    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUED-001", "쿠폰 템플릿을 찾을 수 없습니다."),
    ISSUE_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "ISSUED-002", "현재는 해당 쿠폰을 발급할 수 없습니다."),
    ALREADY_ISSUED(HttpStatus.CONFLICT, "ISSUED-003", "이미 발급된 쿠폰입니다."),

    ISSUED_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUED-004", "발급된 쿠폰을 찾을 수 없습니다."),
    INVALID_OWNER(HttpStatus.FORBIDDEN, "ISSUED-005", "쿠폰에 접근할 권한이 없습니다."),

    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "ISSUED-006", "만료된 쿠폰입니다."),
    INVALID_COUPON_STATUS(HttpStatus.BAD_REQUEST, "ISSUED-007", "사용 가능한 쿠폰 상태가 아닙니다.");

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

    IssuedCouponErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
