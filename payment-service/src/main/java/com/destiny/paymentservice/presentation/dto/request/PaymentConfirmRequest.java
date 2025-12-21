package com.destiny.paymentservice.presentation.dto.request;

import com.destiny.paymentservice.domain.vo.PaymentProvider;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.UUID;

/**
 * 결제 승인 요청 DTO (PENDING -> PAID)
 * 실제 PG사 연동에서는 pgTxId(PG사 거래ID), amount 등 PG사에서 반환한 정보가 추가됩니다.
 */
public record PaymentConfirmRequest(
    UUID orderId,
    UUID userId,
    Integer amount,
    // 여러 PG사의 필드명을 모두 수용합니다.
    @JsonAlias({"paymentKey", "paymentId", "receiptId"})
    String pgTxId,
    PaymentProvider provider
) {}