package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.presentation.dto.request.NotificationLogSearchRequest;
import com.destiny.notificationservice.presentation.dto.request.OrderCreatedNotificationRequest;
import com.destiny.notificationservice.presentation.dto.request.SagaErrorNotificationRequest;
import com.destiny.notificationservice.presentation.dto.response.NotificationLogPageResponse;
import com.destiny.notificationservice.presentation.dto.response.NotificationResultResponse;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResultResponse sendOrderCreatedNotification(
        OrderCreatedNotificationRequest request);

    NotificationResultResponse sendSagaErrorNotification(
        SagaErrorNotificationRequest request);

    NotificationLogPageResponse getNotificationLogs(

        NotificationLogSearchRequest request, Pageable pageable);
}
