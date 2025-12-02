package com.destiny.notificationservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationLogItemResponse (

    UUID logId,
    UUID brandId,
    String message,
    String status,
    Integer responseCode,
    String responseMessage,
    String errorCode,
    String errorMessage,
    LocalDateTime createdAt

) {

}
