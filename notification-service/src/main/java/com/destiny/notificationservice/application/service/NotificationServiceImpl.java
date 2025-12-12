package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
import com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${slack.webhook.admin-url}")
    private String adminSlackUrl;

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
        BrandNotificationChannel channel = notificationChannelRepository.findByBrandId(brandId)
            .orElse(null);

        String logMessage = sanitizeForLog(message);

        if (channel == null || !channel.isActive()) {
            saveLog(
                brandId,
                logMessage,
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
            Map<String, String> payload = Collections.singletonMap("text", logMessage);

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
                logMessage,
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
                logMessage,
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

    private String sanitizeForLog(String message) {
        if (message == null) {
            return null;
        }

        return message.replaceAll(
            "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", "***@$2"
        );

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

    private String nvl(Object value) {
        return value == null ? "없음" : value.toString();
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
            nvl(req.option()),
            req.quantity(),
            req.totalPrice(),
            req.buyerName(),
            req.buyerEmail(),
            nvl(req.message())
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
            nvl(req.errorCode()),
            nvl(req.errorMessage()),
            nvl(req.message())
        );
    }


    @Override
    public void sendSagaCreateFailedNotification(SagaCreateFailedEvent event) {
        String message = String.format(
            "🚨 *[사가 실패 알림]*\n" +
                "주문ID: %s\n" +
                "실패 단계: %s\n" +
                "실패 서비스: %s\n" +
                "에러 코드: %s\n" +
                "실패 사유: %s\n" +
                "상세 메시지: %s",
            event.orderId(),
            event.failStep(),
            event.failService(),
            event.errorCode(),
            event.failReason(),
            event.detailMessage()
        );

        sendToSlack(
            adminSlackUrl,
            message,
            event.errorCode(),
            event.failReason());
    }

    private void sendToSlack(String adminSlackUrl, String message, String errorCode,
        String errorMessage) {

        try {
            Map<String, String> payload = Collections.singletonMap("text", message);
            restTemplate.postForEntity(adminSlackUrl, payload, String.class);

            saveLog(null, message, "SUCCESS", 200, "Sent to Admin", errorCode, errorMessage);
        } catch (Exception e) {
            log.error("Failed to send slack message", e);
            saveLog(null, message, "FAIL", 500, e.getMessage(), errorCode, errorMessage);
        }
    }

    @Override
    public void sendOrderCreateSuccessNotification(OrderCreateSuccessEvent event) {

        Map<UUID, List<OrderCreateSuccessEvent.OrderItem>> itemsByBrand = event.items().stream()
            .collect(Collectors.groupingBy(OrderCreateSuccessEvent.OrderItem::brandId));

        itemsByBrand.forEach((brandId, items) -> {

            int totalQuantity = items.stream()
                .mapToInt(OrderCreateSuccessEvent.OrderItem::stock)
                .sum();

            int totalAmount = items.stream()
                .mapToInt(OrderCreateSuccessEvent.OrderItem::finalAmount)
                .sum();

            String message = String.format(
                "📢 *[신규 주문 알림]*\n" +
                    "주문ID: %s\n" +
                    "유저ID: %s\n" +
                    "브랜드ID: %s\n" +
                    "상품 개수: %d개\n" +
                    "총 수량: %d개\n" +
                    "총 결제 금액: %d원",
                event.orderId(),
                event.userId(),
                brandId,
                items.size(),
                totalQuantity,
                totalAmount
            );

            sendToSlackAndLog(
                brandId,
                message,
                null,
                null
            );
        });


    }
}