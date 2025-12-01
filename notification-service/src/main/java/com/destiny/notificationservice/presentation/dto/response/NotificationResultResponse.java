package com.destiny.notificationservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResultResponse {

    private final String status;
    private final String message;

}
