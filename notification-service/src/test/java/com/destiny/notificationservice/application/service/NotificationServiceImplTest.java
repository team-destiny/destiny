package com.destiny.notificationservice.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.destiny.notificationservice.application.dto.event.OrderCancelRequestedEvent;
import com.destiny.notificationservice.application.dto.event.OrderCreateSuccessEvent;
import com.destiny.notificationservice.domain.model.BrandNotificationChannel;
import com.destiny.notificationservice.domain.repository.NotificationChannelRepository;
import com.destiny.notificationservice.domain.repository.NotificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private SetOperations<String, String> setOps;
    @Mock
    private NotificationChannelRepository notificationChannelRepository;
    @Mock
    private NotificationLogRepository notificationLogRepository;
    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOps);

        notificationService = new NotificationServiceImpl(
            redisTemplate,
            objectMapper,
            notificationChannelRepository,
            notificationLogRepository,
            restTemplate);

        ReflectionTestUtils.setField(notificationService, "adminSlackUrl",
            "https://hooks.slack.com/services/admin");
    }

    @Test
    @DisplayName("주문 성공 알림 - 브랜드별 금액 계산 및 Redis 저장 확인")
    void sendOrderCreateSuccessNotification() {
        // given: 주문 이벤트 및 브랜드 채널 생성
        UUID orderId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();

        OrderCreateSuccessEvent.OrderItem item = new OrderCreateSuccessEvent.OrderItem(
            UUID.randomUUID(), brandId, 10000, 2);

        OrderCreateSuccessEvent event = new OrderCreateSuccessEvent(
            orderId, UUID.randomUUID(), null, 0, 20000, List.of(item), 20000);

        BrandNotificationChannel channel = new BrandNotificationChannel(brandId,
            "https://brand-webhook");
        when(notificationChannelRepository.findByBrandId(brandId)).thenReturn(Optional.of(channel));

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: 알림 전송
        notificationService.sendOrderCreateSuccessNotification(event);

        // then
        // 1. Redis에 브랜드 정보가 저장되었는지 확인
        verify(redisTemplate).delete("order:brands:" + orderId);
        verify(setOps).add("order:brands:" + orderId, brandId.toString());
        verify(redisTemplate).expire(eq("order:brands:" + orderId), any(Duration.class));

        // 2. Slack 전송이 호출되었는지 확인
        verify(restTemplate, times(1)).postForEntity(eq("https://brand-webhook"), any(),
            eq(String.class));

        // 3. 로그 저장이 호출되었는지 확인
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("주문 취소 요청 - Redis 캐시 있으면 브랜드 채널로 전송")
    void sendOrderCancelRequestedNotification_withCache() {
        // given: Redis에 캐시된 브랜드 정보
        UUID orderId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        OrderCancelRequestedEvent event = new OrderCancelRequestedEvent(orderId, UUID.randomUUID(),
            20000, "취소 요청");

        when(setOps.members("order:brands:" + orderId)).thenReturn(Set.of(brandId.toString()));

        BrandNotificationChannel channel = new BrandNotificationChannel(brandId,
            "https://brand-webhook");
        when(notificationChannelRepository.findByBrandId(brandId)).thenReturn(Optional.of(channel));

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: 취소 알림 전송
        notificationService.sendOrderCancelRequestedNotification(event);

        // then: 브랜드 채널로 전송 검증
        verify(restTemplate).postForEntity(eq("https://brand-webhook"), any(), eq(String.class));
    }

    @Test
    @DisplayName("주문 취소 요청 - Redis 캐시 없으면 관리자 채널로 전송 (Fallback)")
    void sendOrderCancelRequestedNotification_noCache() {
        // given: Redis 캐시 없음
        UUID orderId = UUID.randomUUID();
        OrderCancelRequestedEvent event = new OrderCancelRequestedEvent(orderId, UUID.randomUUID(),
            20000, "취소 요청");

        when(setOps.members("order:brands:" + orderId)).thenReturn(Set.of());

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: 취소 알림 전송
        notificationService.sendOrderCancelRequestedNotification(event);

        // then: 관리자 채널로 Fallback
        verify(restTemplate).postForEntity(eq("https://hooks.slack.com/services/admin"), any(),
            eq(String.class));
    }

    @Test
    @DisplayName("주문 취소 실패 알림 - 관리자 채널로 전송 확인")
    void sendOrderCancelFailedNotification() {
        // given: 취소 실패 이벤트
        com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent event =
            new com.destiny.notificationservice.application.dto.event.OrderCancelFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PAYMENT_SERVICE",
                "잔액 부족"
            );

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: 실패 알림 전송
        notificationService.sendOrderCancelFailedNotification(event);

        // then: 관리자 채널 전송 및 로그 저장
        verify(restTemplate).postForEntity(eq("https://hooks.slack.com/services/admin"), any(),
            eq(String.class));
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("사가 실패 알림")
    void sendSagaCreateFailedNotification() {
        // given: 사가 실패 이벤트
        com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent event =
            new com.destiny.notificationservice.application.dto.event.SagaCreateFailedEvent(
                UUID.randomUUID(),
                "INVENTORY_REDUCE",
                "InventoryService",
                "OUT_OF_STOCK",
                "재고 없음",
                "상품 ID 123 재고 부족함"
            );

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: 사가 실패 알림 전송
        notificationService.sendSagaCreateFailedNotification(event);

        // then: 관리자 채널 전송 및 로그 저장
        verify(restTemplate).postForEntity(eq("https://hooks.slack.com/services/admin"), any(),
            eq(String.class));
        verify(notificationLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("DLQ 메시지 알림")
    void sendDlqNotification() {
        // given: DLQ 메시지 이벤트
        com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent event =
            new com.destiny.notificationservice.application.dto.event.NotificationDlqMessageEvent(
                "notification.order-create-succeed",
                "notification-dlq-message",
                0,
                123L,
                "notification-group",
                "key",
                "{\"items\": []}", // JSON Payload
                "java.lang.RuntimeException",
                "FAIL",
                3,
                java.time.LocalDateTime.now()
            );

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok("ok"));

        // when: DLQ 알림 전송
        notificationService.sendDlqNotification(event);

        // then: 관리자 채널 전송
        verify(restTemplate).postForEntity(eq("https://hooks.slack.com/services/admin"), any(),
            eq(String.class));
    }
}