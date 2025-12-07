package com.destiny.userservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResponseCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-ERROR-001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER-ERROR-002", "이미 존재하는 사용자명입니다."),
    INVALID_ADMIN_TOKEN(HttpStatus.UNAUTHORIZED, "USER-ERROR-003", "관리자 토큰이 일치하지 않습니다."),
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER-ERROR-004", "이메일 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "USER-ERROR-005", "기존 비밀번호가 일치하지 않습니다."),
    USER_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "USER-ERROR-006", "토큰이 만료되었거나 유효하지 않습니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "USER-ERROR-007", "리프레시 토큰이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
