package com.destiny.notificationservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaErrorNotificationRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID brandId;

    @NotBlank
    private String stage; // 어떤 단계에서 실패?

    @NotBlank
    private String errorCode; // 사가 에러 코드

    @NotBlank
    private String errorMessage;

    @NotBlank
    private String message;

}
