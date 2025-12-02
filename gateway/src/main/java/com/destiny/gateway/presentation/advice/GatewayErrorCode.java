package com.destiny.gateway.presentation.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements GlobalError {
    GATEWAY_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "GW-001", "토큰이 만료되었거나 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GW-002", "서버 내부 오류입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }
}
