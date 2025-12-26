package com.destiny.paymentservice.presentation.dto.request.pg.bootpay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 프론트엔드에서 성공 후 서버로 승인을 요청할 때 보내는 데이터
 */
public record BootPayConfirmRequest(
    @NotBlank(message = "영수증 ID는 필수입니다.")
    String receiptId,

    @NotNull(message = "주문 번호는 필수입니다.")
    UUID orderId,

    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId
) {}