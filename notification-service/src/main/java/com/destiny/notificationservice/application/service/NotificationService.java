package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
import com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
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


    void sendSagaCreateFailedNotification(SagaCreateFailedEvent event);

    void sendOrderCreateSuccessNotification(OrderCreateSuccessEvent event);

    void sendOrderCancelRequestedNotification(OrderCancelRequestedEvent event);

    void sendOrderCancelFailedNotification(OrderCancelFailedEvent event);


    void sendDlqNotification(NotificationDlqMessageEvent event);
}
