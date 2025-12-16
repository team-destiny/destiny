package com.destiny.paymentservice.presentation.code;

import com.destiny.global.code.ResponseCode;
import lombok.Generated;
import org.springframework.http.HttpStatus;

public enum PaymentSuccessCode implements ResponseCode {

    PAYMENT_REQUEST_SUCCESS(HttpStatus.OK, "PAYM-001", "결제 요청이 완료되었습니다."),
    PAYMENT_CONFIRM_SUCCESS(HttpStatus.OK, "PAYM-002", "결제가 정상적으로 완료되었습니다."),
    PAYMENT_CANCEL_SUCCESS(HttpStatus.OK, "PAYM-003", "결제 취소가 완료되었습니다."),
    PAYMENT_INQUIRY_SUCCESS(HttpStatus.OK, "PAYM-004", "결제 조회가 완료되었습니다."),
    PAYMENT_ALL_INQUIRY_SUCCESS(HttpStatus.OK, "PAYM-005", "결제 목록 조회가 완료되었습니다."),
    PAYMENT_PARTIAL_CANCEL_SUCCESS(HttpStatus.OK, "PAYM-006", "부분 취소가 완료되었습니다."),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.OK, "PAYM-007", "이미 결제가 완료된 주문입니다.");

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

    PaymentSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
