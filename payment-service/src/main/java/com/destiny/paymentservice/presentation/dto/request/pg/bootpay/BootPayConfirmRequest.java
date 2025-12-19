package com.destiny.paymentservice.presentation.dto.request.pg.bootpay;

import java.util.UUID;

/**
 * 프론트엔드에서 성공 후 서버로 승인을 요청할 때 보내는 데이터
 */
public record BootPayConfirmRequest(
    String receiptId, // 부트페이가 준 영수증 ID
    UUID orderId,
    UUID userId
) {}