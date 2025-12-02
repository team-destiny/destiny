package com.destiny.notificationservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SagaErrorNotificationRequest(

    @NotNull UUID orderId,
    @NotNull UUID brandId,
    @NotBlank String stage, // 어떤 단계에서 실패?
    @NotBlank String errorCode, // 사가 에러 코드
    @NotBlank String errorMessage,
    @NotBlank String message

) {

}