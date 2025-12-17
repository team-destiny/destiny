package com.destiny.paymentservice.presentation.dto.response.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentsResponse(

    // ===== 기본 식별 =====
    String paymentKey,
    String orderId,
    String status,          // DONE, CANCELED, FAILED 등
    String method,          // 카드, 가상계좌, 계좌이체 ...

    // ===== 금액 =====
    Integer totalAmount,
    Integer balanceAmount,
    Integer suppliedAmount,
    Integer vat,
    Integer taxFreeAmount,

    // ===== 시간 =====
    OffsetDateTime approvedAt,
    OffsetDateTime requestedAt,

    // ===== 카드 결제 =====
    Card card,

    // ===== 가상계좌 =====
    VirtualAccount virtualAccount,

    // ===== 간편결제 =====
    EasyPay easyPay

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Card(
        String company,
        String number,
        String installmentPlanMonths,
        Boolean isInterestFree,
        String approveNo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VirtualAccount(
        String accountNumber,
        String bank,
        String customerName,
        OffsetDateTime dueDate
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EasyPay(
        String provider,
        Integer amount,
        Integer discountAmount
    ) {}
}