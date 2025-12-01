package com.destiny.notificationservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationLogPageResponse {

    private final UUID logId;
    private final UUID brandId;
    private final String message;

    private final String status;

    private final Integer responseCode;
    private final String responseMessage;
    private final String errorCode;
    private final String errorMessage;
    private final LocalDateTime createdAt;
}
