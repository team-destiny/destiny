package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.presentation.dto.request.NotificationLogSearchRequest;
import com.destiny.notificationservice.presentation.dto.request.OrderCreatedNotificationRequest;
import com.destiny.notificationservice.presentation.dto.request.SagaErrorNotificationRequest;
import com.destiny.notificationservice.presentation.dto.response.NotificationLogPageResponse;
import com.destiny.notificationservice.presentation.dto.response.NotificationResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Override
    public NotificationResultResponse sendOrderCreatedNotification(
        OrderCreatedNotificationRequest request
    ) {
        return new NotificationResultResponse("SUCCESS", "Notification request accepted");

    }

    @Override
    public NotificationResultResponse sendSagaErrorNotification(
        SagaErrorNotificationRequest request
    ) {
        return new NotificationResultResponse("SUCCESS", "Saga error notification received");

    }

    @Override
    @Transactional(readOnly = true)
    public NotificationLogPageResponse getNotificationLogs(
        NotificationLogSearchRequest searchRequest,
        Pageable pageable
    ) {
        throw new UnsupportedOperationException("아직 미구현된 메서드입니다.");
    }
}
