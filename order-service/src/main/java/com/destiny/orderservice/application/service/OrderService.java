package com.destiny.orderservice.application.service;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderStatus;
import com.destiny.orderservice.domain.repository.OrderRepository;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest.OrderItemCreateRequest;
import com.destiny.orderservice.presentation.dto.response.OrderDetailResponse;
import com.destiny.orderservice.presentation.dto.response.OrderProcessingResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public UUID createOrder(OrderCreateRequest req) {

        Order order = Order.of(
            UUID.randomUUID(),
            req.couponId(),
            req.paymentMethod(),
            req.recipientName(),
            req.recipientPhone(),
            req.zipcode(),
            req.address1(),
            req.address2(),
            req.deliveryMessage()
        );

        for (OrderItemCreateRequest itemReq : req.items()) {
            OrderItem orderItem = OrderItem.of(
                itemReq.productId(),
                itemReq.itemPromotionId(),
                itemReq.stock()
            );
            order.addItem(orderItem);
        }

        // TODO : 사가 오케스트레이션으로 카프카 메시지 발송

        return orderRepository.createOrder(order).getOrderId();
    }

    @Transactional(readOnly = true)
    public Object getOrderDetail(UUID orderId) {

        Order order = orderRepository.findOrderWithItems(orderId).orElseThrow(
            () -> new RuntimeException("주문을 찾을 수 없습니다.")
        );

        // 사가 처리 전 상태 : CREATE
        if (order.getOrderStatus().equals(OrderStatus.CREATED)) {
            return OrderProcessingResponse.of(order.getOrderId(), order.getOrderStatus());
        }

        return OrderDetailResponse.fromEntity(order);
    }
}
