package com.destiny.userservice.domain.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResponseCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER-002", "이미 존재하는 사용자명입니다."),
    INVALID_ADMIN_TOKEN(HttpStatus.BAD_REQUEST, "USER-003", "관리자 토큰이 일치하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
