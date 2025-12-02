package com.destiny.notificationservice.presentation.dto.request;

import java.util.UUID;

public record NotificationLogSearchRequest (

    UUID brandId,
    String status

) {

}