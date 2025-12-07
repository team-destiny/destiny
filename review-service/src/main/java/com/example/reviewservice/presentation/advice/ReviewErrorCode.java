package com.example.reviewservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ResponseCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_ERROR_001", "리뷰를 찾을 수 없습니다."),
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "REVIEW_ERROR_002", "productId와 userId는 동시에 사용할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
