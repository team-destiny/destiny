package com.destiny.notificationservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationLogPageResponse {

    private final List<NotificationLogItemResponse> content;

    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;
}
