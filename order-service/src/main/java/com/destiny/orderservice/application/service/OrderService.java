package com.destiny.orderservice.application.service;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.repository.OrderRepository;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest.OrderItemCreateRequest;
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

        return orderRepository.createOrder(order).getOrderId();
    }
}
