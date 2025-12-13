package com.destiny.couponservice.presentation.advice;

import com.destiny.global.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponTemplateSuccessCode implements ResponseCode {

    COUPON_TEMPLATE_CREATE(HttpStatus.CREATED, "COUPON-SUCCESS-001", "쿠폰 템플릿이 생성되었습니다."),
    COUPON_TEMPLATE_GET(HttpStatus.OK, "COUPON-SUCCESS-002", "쿠폰템플릿 상세조회가 완료되었습니다."),
    COUPON_TEMPLATE_LIST_GET(HttpStatus.OK, "COUPON-SUCCESS-003", "쿠폰템플릿 목록조회가 완료되었습니다."),
    COUPON_TEMPLATE_UPDATE(HttpStatus.OK, "COUPON-SUCCESS-004", "쿠폰 템플릿이 수정되었습니다."),
    COUPON_TEMPLATE_DELETE(HttpStatus.OK, "COUPON-SUCCESS-005", "쿠폰 템플릿이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;


}
