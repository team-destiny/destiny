package com.destiny.paymentservice.presentation.dto.response.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // 모든 필드를 snake_case로 자동 매핑
public record BootPayReceiptResponse(
    String receiptId,      // JSON의 receipt_id와 매핑
    String orderId,        // JSON의 order_id와 매핑
    Integer price,
    String orderName,      // JSON의 order_name과 매핑
    String pg,
    String method,
    String methodSymbol,   // JSON의 method_symbol과 매핑
    Integer status,
    String statusLocale,   // JSON의 status_locale과 매핑
    OffsetDateTime purchasedAt, // JSON의 purchased_at과 매핑

    CardData cardData,     // JSON의 card_data와 매핑
    BankData bankData,     // JSON의 bank_data와 매핑
    VBankData vbankData    // JSON의 vbank_data와 매핑
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CardData(
        String cardCompany,
        String cardNo,
        String cardApproveNo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record BankData(
        String bankName,
        String bankCode
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record VBankData(
        String bankName,
        String bankAccount,
        String bankUsername,
        OffsetDateTime expiredAt
    ) {}
}