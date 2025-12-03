package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.domain.model.BrandNotificationChannel;
import com.destiny.notificationservice.domain.model.BrandNotificationLog;
import com.destiny.notificationservice.domain.repository.NotificationChannelRepository;
import com.destiny.notificationservice.domain.repository.NotificationLogRepository;
import com.destiny.notificationservice.presentation.dto.request.NotificationLogSearchRequest;
import com.destiny.notificationservice.presentation.dto.request.OrderCreatedNotificationRequest;
import com.destiny.notificationservice.presentation.dto.request.SagaErrorNotificationRequest;
import com.destiny.notificationservice.presentation.dto.response.NotificationLogItemResponse;
import com.destiny.notificationservice.presentation.dto.response.NotificationLogPageResponse;
import com.destiny.notificationservice.presentation.dto.response.NotificationResultResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";

    private final NotificationChannelRepository notificationChannelRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final RestTemplate restTemplate;

    @Override
    public NotificationResultResponse sendOrderCreatedNotification(
        OrderCreatedNotificationRequest request
    ) {
        String slackMessage = formatOrderMessage(request);

        return sendToSlackAndLog(
            request.brandId(),
            slackMessage,
            null,
            null
        );

    }

    @Override
    public NotificationResultResponse sendSagaErrorNotification(
        SagaErrorNotificationRequest request
    ) {
        String slackMessage = formatSagaErrorMessage(request);

        return sendToSlackAndLog(
            request.brandId(),
            slackMessage,
            request.errorCode(),
            request.errorMessage()
        );

    }

    private NotificationResultResponse sendToSlackAndLog(
        UUID brandId,
        String message,
        String errorCode,
        String errorMessage
    ) {
        BrandNotificationChannel channel = notificationChannelRepository.findByBrandId(brandId).orElse(null);

        if (channel == null || !channel.isActive()) {
            saveLog(
                brandId,
                message,
                STATUS_FAIL,
                404,
                "Channel not found or inactive",
                "CHANNEL_NOT_AVAILABLE",
                "No active notification channel for brand: " + brandId
            );
            return new NotificationResultResponse(
                STATUS_FAIL,
                "Slack channel not found."
            );
        }

        try {
            Map<String, String> payload = Collections.singletonMap("text", message);

            ResponseEntity<String> response = restTemplate.postForEntity(
                channel.getSlackUrl(),
                payload,
                String.class
            );

            int statusCode = response.getStatusCode().value();
            boolean success = response.getStatusCode().is2xxSuccessful();
            String responseBody = response.getBody();

            saveLog(
                brandId,
                message,
                success ? STATUS_SUCCESS : STATUS_FAIL,
                statusCode,
                responseBody,
                errorCode,
                errorMessage
            );

            if (success) {
                return new NotificationResultResponse(
                    STATUS_SUCCESS,
                    "Notification sent."
                );
            } else {
                return new NotificationResultResponse(
                    STATUS_FAIL,
                    "Slack returned non-2xx status."
                );
            }
        } catch (Exception e) {
            log.error("Slack sending failed for BrandId={}", brandId, e);

            saveLog(
                brandId,
                message,
                STATUS_FAIL,
                500,
                "Slack request failed",
                "SLACK_SEND_FAILED",
                e.getMessage()
            );

            return new NotificationResultResponse(
                STATUS_FAIL,
                "Slack request failed."
            );
        }
    }

    private void saveLog(
        UUID brandId,
        String message,
        String status,
        Integer responseCode,
        String responseMsg,
        String errorCode,
        String errorMessage
    ) {
        BrandNotificationLog logEntity = BrandNotificationLog.builder()
            .brandId(brandId)
            .message(message)
            .status(status)
            .responseCode(responseCode)
            .responseMessage(responseMsg)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();

        notificationLogRepository.save(logEntity);
    }



    @Override
    @Transactional(readOnly = true)
    public NotificationLogPageResponse getNotificationLogs(
        NotificationLogSearchRequest searchRequest,
        Pageable pageable
    ) {
        Page<BrandNotificationLog> pageResult = notificationLogRepository.findAllBySearch(
            searchRequest.brandId(),
            searchRequest.status(),
            pageable
        );

        List<NotificationLogItemResponse> content = pageResult.getContent().stream()
            .map(log -> new NotificationLogItemResponse(
                log.getId(),
                log.getBrandId(),
                log.getMessage(),
                log.getStatus(),
                log.getResponseCode(),
                log.getResponseMessage(),
                log.getErrorCode(),
                log.getErrorMessage(),
                log.getCreatedAt()
            ))
            .toList();

        return new NotificationLogPageResponse(
            content,
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages()
        );
    }


    private String formatOrderMessage(OrderCreatedNotificationRequest req) {
        return String.format(
            "📢 *[신규 주문 알림]*\n" +
                "주문번호: %s\n" +
                "브랜드ID: %s\n" +
                "상품: %s (%s)\n" +
                "수량: %d개 / 금액: %d원\n" +
                "구매자: %s (%s)\n" +
                "메시지: %s",
            req.orderNumber(),
            req.brandId(),
            req.productName(),
            req.option(),
            req.quantity(),
            req.totalPrice(),
            req.buyerName(),
            req.buyerEmail(),
            req.message()
        );
    }

    private String formatSagaErrorMessage(SagaErrorNotificationRequest req) {
        return String.format(
            "🚨 *[주문 처리 실패]*\n" +
                "주문ID: %s\n" +
                "브랜드ID: %s\n" +
                "실패 단계: %s\n" +
                "에러코드: %s\n" +
                "사유: %s\n" +
                "상세 메시지: %s",
            req.orderId(),
            req.brandId(),
            req.stage(),
            req.errorCode(),
            req.errorMessage(),
            req.message()
        );
    }
}