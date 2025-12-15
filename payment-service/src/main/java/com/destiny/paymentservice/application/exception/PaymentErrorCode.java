package com.destiny.paymentservice.application.exception;

import com.destiny.global.code.ResponseCode;
import lombok.Generated;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ResponseCode {

    PAYMENT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "PAYM-001", "잘못된 결제 요청입니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "PAYM-002", "결제 승인에 실패했습니다."),
    PAYMENT_ALREADY_APPROVED(HttpStatus.CONFLICT, "PAYM-003", "이미 승인된 결제입니다."),
    PAYMENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "PAYM-004", "이미 취소된 결제입니다."),
    PAYMENT_NOT_PENDING(HttpStatus.BAD_REQUEST, "PAYM-005", "결제 승인 대기 상태(PENDING)가 아닙니다."),
    PAYMENT_NOT_PAID(HttpStatus.BAD_REQUEST, "PAYM-006", "결제 완료 상태(PAID)가 아닙니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYM-007", "요청하신 주문 번호의 결제 내역을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "PAYM-008", "해당 결제에 접근할 권한이 없습니다."),
    PAYMENT_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PAYM-009", "결제 정보의 소유자가 일치하지 않아 요청을 처리할 수 없습니다."),
    UNSUPPORTED_PAYMENT_PROVIDER(HttpStatus.BAD_REQUEST, "PAYM-010", "지원하지 않는 결제대행사입니다.");

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

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}