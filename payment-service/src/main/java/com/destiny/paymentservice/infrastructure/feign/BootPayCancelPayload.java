package com.destiny.paymentservice.infrastructure.feign;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BootPayCancelPayload {
    private String receiptId;      // 부트페이 영수증 ID (pgTxId)
    private Integer cancelPrice;   // 취소할 금액
    private String cancelReason;   // 취소 사유
}