package com.destiny.notificationservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;  // 추가
import com.destiny.global.response.ApiResponse;  // 추가 (global)
import com.destiny.notificationservice.application.service.NotificationService;
import com.destiny.notificationservice.infrastructure.security.auth.CustomUserDetails;  // 인증용
import com.destiny.notificationservice.presentation.dto.request.NotificationLogSearchRequest;
import com.destiny.notificationservice.presentation.dto.request.OrderCreatedNotificationRequest;
import com.destiny.notificationservice.presentation.dto.request.SagaErrorNotificationRequest;
import com.destiny.notificationservice.presentation.dto.response.NotificationLogPageResponse;
import com.destiny.notificationservice.presentation.dto.response.NotificationResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;  // 추가 (ADMIN 권한)
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/brand-notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /*
    * 주문 알림 발송
    * */
    @PostMapping("/order-created")
    public ResponseEntity<ApiResponse<NotificationResultResponse>> sendOrderCreatedNotification(
        @RequestBody @Valid OrderCreatedNotificationRequest request
    ) {
        NotificationResultResponse response =
            notificationService.sendOrderCreatedNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }

    /*
    * saga 실패 알림 발송
    * */
    @PostMapping("/saga-error")
    public ResponseEntity<ApiResponse<NotificationResultResponse>> sendSagaErrorNotification(
        @RequestBody @Valid SagaErrorNotificationRequest request
    ) {
        NotificationResultResponse response =
            notificationService.sendSagaErrorNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }


    /*
    * 알림 발송 로그 조회
    * */
    @PreAuthorize("hasAnyRole('MASTER', 'PARTNER')")
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<NotificationLogPageResponse>> getNotificationLogs(
        @Valid NotificationLogSearchRequest searchRequest,
        Pageable pageable
    ) {
        NotificationLogPageResponse response =
            notificationService.getNotificationLogs(searchRequest, pageable);

        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

}
