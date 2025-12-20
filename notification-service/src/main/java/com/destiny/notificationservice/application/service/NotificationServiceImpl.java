package com.destiny.notificationservice.application.service;

import com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";

    private final StringRedisTemplate stringRedisTemplate;

    private static final String ORDER_BRANDS_KEY_PREFIX = "order:brands:";

    @Value("${slack.webhook.admin-url:}")
    private String adminSlackUrl;

    private final ObjectMapper objectMapper;

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
        BrandNotificationChannel channel = null;
        if (brandId != null) {
            channel = notificationChannelRepository.findByBrandId(brandId).orElse(null);
        }

        String logMessage = sanitizeForLog(message);

        String targetUrl =
            (channel != null && channel.isActive() && StringUtils.hasText(channel.getSlackUrl()))
                ? channel.getSlackUrl()
                : this.adminSlackUrl;

        if (targetUrl == null || targetUrl.isBlank()) {
            saveLog(
                brandId,
                logMessage,
                STATUS_FAIL,
                500,
                "Env Var is empty",
                "ENV_ERR",
                "No webhook url");
            return new NotificationResultResponse(STATUS_FAIL, "No Slack URL configured.");
        }

        try {
            Map<String, String> payload = Collections.singletonMap("text", logMessage);

            ResponseEntity<String> response = restTemplate.postForEntity(
                targetUrl,
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

        sendAdminToSlack(
            adminSlackUrl,
            message,
            event.errorCode(),
            event.failReason());
    }

    private void sendAdminToSlack(String adminSlackUrl, String message, String errorCode,
        String errorMessage) {

        if (adminSlackUrl == null || adminSlackUrl.isBlank()) {
            log.warn("Slack url is null or empty");
            saveLog(
                null,
                sanitizeForLog(message),
                STATUS_FAIL,
                500,
                "Admin Slack url not configured",
                errorCode,
                errorMessage);

            return;

        }

        String logMessage = sanitizeForLog(message);

        try {
            Map<String, String> payload = Collections.singletonMap("text", message);
            ResponseEntity<String> response = restTemplate.postForEntity(adminSlackUrl, payload,
                String.class);

            int statusCode = response.getStatusCode().value();
            boolean success = response.getStatusCode().is2xxSuccessful();

            saveLog(
                null,
                logMessage,
                success ? STATUS_SUCCESS : STATUS_FAIL,
                statusCode,
                response.getBody(),
                errorCode,
                errorMessage);

        } catch (Exception e) {
            log.error("Failed to send admin slack message", e);
            saveLog(
                null,
                logMessage,
                STATUS_FAIL,
                500,
                "Slack request failed",
                "SLACK_SEND_FAILED",
                e.getMessage()
            );
        }
    }

    @Override
    public void sendOrderCreateSuccessNotification(OrderCreateSuccessEvent event) {

        UUID unknownBrandId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Map<UUID, List<OrderCreateSuccessEvent.OrderItem>> itemsByBrand =
            event.items().stream()
                .collect(Collectors.groupingBy(item ->
                    item.brandId() != null ? item.brandId() : unknownBrandId));

        Set<UUID> brandIds = itemsByBrand.keySet().stream()
            .filter(id -> id != null && !id.equals(unknownBrandId))
            .collect(Collectors.toSet());

        cacheOrderBrands(event.orderId(), brandIds);

        int totalAmount = (event.finalAmount() == null) ? 0 : event.finalAmount();

        itemsByBrand.forEach((brandId, items) -> {
            int totalQuantity = items.stream()
                .mapToInt(item -> item.stock() != null ? item.stock() : 0)
                .sum();

            int brandAmount = items.stream()
                .mapToInt(item -> (item.price() != null ? item.price() : 0) * (item.stock() != null
                    ? item.stock() : 0))
                .sum();

            String message = String.format(
                "📢 *[신규 주문 알림]*\n" +
                    "주문ID: %s\n" +
                    "유저ID: %s\n" +
                    "브랜드ID: %s\n" +
                    "상품 개수: %d개\n" +
                    "총 주문 수량: %d개\n" +
                    "브랜드 주문 금액: %d원",
                event.orderId(),
                event.userId(),
                brandId.equals(unknownBrandId) ? "알수없음(NULL)" : brandId,
                items.size(),
                totalQuantity,
                brandAmount
            );

            sendToSlackAndLog(
                brandId.equals(unknownBrandId) ? null : brandId,
                message,
                null,
                null
            );
        });
    }


    @Override
    public void sendOrderCancelRequestedNotification(OrderCancelRequestedEvent event) {

        String message = formatOrderCancelRequestedMessage(event);

        Set<String> brandIdStrings = getCachedBrandIds(event.orderId());

        if (brandIdStrings.isEmpty()) {
            sendAdminToSlack(
                adminSlackUrl,
                message,
                null,
                null);
            return;
        }

        for (String s : brandIdStrings) {
            try {
                UUID brandId = UUID.fromString(s);
                sendToSlackAndLog(brandId, message, null, null);
            } catch (IllegalArgumentException e) {
                log.warn("[Redis] 브랜드ID 캐시 값이 UUID 형식이 아님. orderId={}, cachedValue={}",
                    event.orderId(), s);
            }
        }
    }

    private String formatOrderCancelRequestedMessage(OrderCancelRequestedEvent event) {
        return String.format(
            "🚫 *[주문 취소 요청]*\n" +
                "주문ID: %s\n" +
                "유저ID: %s\n" +
                "최종 결제 금액: %s원\n" +
                "메시지: %s",
            event.orderId(),
            event.userId(),
            nvl(event.finalAmount()),
            nvl(event.message())
        );
    }


    @Override
    public void sendOrderCancelFailedNotification(OrderCancelFailedEvent event) {

        String message = formatOrderCancelFailedMessage(event);

        sendAdminToSlack(
            adminSlackUrl,
            message,
            event.failStep(),
            event.failReason()
        );
    }

    private String formatOrderCancelFailedMessage(OrderCancelFailedEvent event) {
        return String.format(
            "⚠️ *[주문 취소 실패]*\n" +
                "주문ID: %s\n" +
                "유저ID: %s\n" +
                "실패 단계: %s\n" +
                "실패 사유: %s",
            event.orderId(),
            event.userId(),
            nvl(event.failStep()),
            nvl(event.failReason())
        );
    }

    public void sendDlqNotification(NotificationDlqMessageEvent event) {

        String payloadPreview = formatPayloadForSlack(event.messagePayload(), 1000);

        String message = String.format(
            "🧯 *[DLQ 적재]*\n" +
                "원본 토픽: %s\n" +
                "컨슈머 그룹: %s\n" +
                "재시도: %s회\n" +
                "예외: %s\n" +
                "위치: p%s / o%s\n" +
                "시간: %s\n" +
                "payload(일부):\n%s",
            nvl(event.originalTopic()),
            nvl(event.consumerGroup()),
            nvl(event.retryCount()),
            nvl(event.exceptionType()),
            nvl(event.partitionNumber()),
            nvl(event.offsetNumber()),
            nvl(event.createdAt()),
            payloadPreview
        );

        sendAdminToSlack(adminSlackUrl, message, "DLQ_MESSAGE", nvl(event.exceptionType()));
    }

    private String formatPayloadForSlack(String raw, int maxLen) {
        if (raw == null) return "없음";

        String trimmed = raw.trim();
        try {
            Object json = objectMapper.readValue(trimmed, Object.class);
            String formatted = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

            if (formatted.length() > maxLen) {
                formatted = formatted.substring(0, maxLen) + "\n(생략)";
            }
            return formatted;
        } catch (Exception ignore) {
            if (trimmed.length() > maxLen) trimmed = trimmed.substring(0, maxLen) + "(생략)";
            return trimmed;
        }
    }


    private void cacheOrderBrands(UUID orderId, Set<UUID> brandIds) {
        if (orderId == null || brandIds == null || brandIds.isEmpty()) {
            return;
        }

        String key = ORDER_BRANDS_KEY_PREFIX + orderId;

        stringRedisTemplate.delete(key);

        for (UUID brandId : brandIds) {
            if (brandId != null) {
                stringRedisTemplate.opsForSet().add(key, brandId.toString());
            }
        }

        stringRedisTemplate.expire(key, Duration.ofDays(7));
    }

    private Set<String> getCachedBrandIds(UUID orderId) {
        if (orderId == null) {
            return Set.of();
        }
        String key = ORDER_BRANDS_KEY_PREFIX + orderId;

        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        return (members == null) ? Set.of() : members;
    }

}