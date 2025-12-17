package com.destiny.cartservice.infrastructure.messaging.consumer;

import com.destiny.cartservice.application.dto.event.CartClearEvent;
import com.destiny.cartservice.application.service.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCartConsumer {

    private final CartService cartService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.cart-clear-request}")
    public void handleCartClearEvent(String message) {

        try{
            CartClearEvent event = objectMapper.readValue(message, CartClearEvent.class);

            log.info("[Kafka] 장바구니 비우기 이벤트 수신. cartId: {}", event.cartId());

            cartService.clearCart(event);

            log.info("[Kafka] 장바구니 비우기 및 캐시 무효화 완료. cartId: {}", event.cartId());

        } catch (JsonProcessingException e) {

            log.error("[Kafka] 장바구니 비우기 중 에러. 원본 메시지: {}", message, e);
        }
    }
}
