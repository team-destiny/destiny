package com.destiny.brandservice.infrastructure.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BrandError implements ResponseCode {

    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "BRAND-001", "해당 브랜드를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    BrandError(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
