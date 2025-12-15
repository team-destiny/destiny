package com.destiny.orderservice.presentation.dto.response;

import com.destiny.orderservice.domain.entity.OrderStatus;
import java.util.Map;

public class OrderStatusMessage {

    private static final Map<OrderStatus, String> messages = Map.of(
        OrderStatus.PENDING, "주문이 생성되었습니다. 잠시만 기다려주세요.",
        OrderStatus.CREATED, "주문을 처리하고 있습니다. 잠시만 기다려주세요.",
        OrderStatus.WAITING_PAYMENT, "결제를 대기하고 있습니다.",
        OrderStatus.COMPLETED, "주문이 완료되었습니다.",
        OrderStatus.FAILED, "주문요청이 실패하였습니다.",
        OrderStatus.CANCELED, "주문 취소가 완료되었습니다."
    );

    public static String getMessage(OrderStatus status) {
        return messages.getOrDefault(status, "처리 중입니다.");
    }

}
