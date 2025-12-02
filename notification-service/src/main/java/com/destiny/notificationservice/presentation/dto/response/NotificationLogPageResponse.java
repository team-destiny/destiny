package com.destiny.notificationservice.presentation.dto.response;

import java.util.List;

public record NotificationLogPageResponse(

    List<NotificationLogItemResponse> content,

    int page,
    int size,
    long totalItems,
    int totalPages

) {
}
