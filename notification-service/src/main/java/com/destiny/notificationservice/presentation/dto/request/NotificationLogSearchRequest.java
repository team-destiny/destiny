package com.destiny.notificationservice.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationLogSearchRequest {

    private UUID brandId;
    private String status;

}
