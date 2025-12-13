package com.destiny.couponservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum IssuedCouponSuccessCode implements ResponseCode {

    COUPON_CREATE(HttpStatus.CREATED, "ISSUED-001", "쿠폰이 발급되었습니다."),
    COUPON_GET(HttpStatus.OK, "ISSUED-002", "쿠폰 상세조회가 완료되었습니다."),
    COUPON_LIST_GET(HttpStatus.OK, "ISSUED-003", "쿠폰 목록조회가 완료되었습니다."),
    COUPON_CANCEL(HttpStatus.OK, "ISSUED-004", "쿠폰사용 취소가 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;


}
