package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.Map;

public class OrderStatusMessage {

    private static final Map<OrderStatus, String> messages = Map.of(
        OrderStatus.CREATED, "주문을 처리하고 있습니다. 잠시만 기다려주세요.",
        OrderStatus.WAITING_PAYMENT, "결제를 대기하고 있습니다."
    );

    public static String getMessage(OrderStatus status) {
        return messages.getOrDefault(status, "처리 중입니다.");
    }

}
