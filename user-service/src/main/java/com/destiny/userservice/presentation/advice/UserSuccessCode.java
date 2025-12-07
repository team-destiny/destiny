package com.destiny.userservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements ResponseCode {
    LOGOUT(HttpStatus.OK, "USER-SUCCESS-001", "모든 기기에서 로그아웃되었습니다."),
    USER_GET(HttpStatus.OK, "USER-SUCCESS-002", "사용자 조회가 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
