package com.destiny.paymentservice.presentation.dto.response.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PortOneResponse(
    String id,                // 포트원 결제 고유 ID (paymentId)
    String status,            // 결제 상태 (READY, PAID, CANCELLED, FAILED)
    String orderName,         // 주문명
    Amount amount,            // 금액 상세
    PaymentMethod method,     // 결제 수단 상세
    OffsetDateTime paidAt,    // 결제 완료 시점
    String customerId         // 고객 ID
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Amount(
        Integer total,        // 총 결제 금액
        Integer taxFree,      // 면세액
        Integer vat,          // 부가세
        Integer supply        // 공급가액
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentMethod(
        String type,          // CARD, TRANSFER, VIRTUAL_ACCOUNT 등
        Card card,            // type이 CARD일 경우
        EasyPay easyPay       // 간편결제 정보
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Card(
        String name,          // 카드사 이름
        String number,        // 카드 번호 (일부 마스킹)
        String approvalCode   // 승인 번호
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EasyPay(
        String provider       // 카카오페이, 네이버페이 등
    ) {}
}