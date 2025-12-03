package com.destiny.orderservice.application.service;

import com.destiny.global.exception.BizException;
import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderItemStatus;
import com.destiny.orderservice.domain.entity.OrderStatus;
import com.destiny.orderservice.domain.repository.OrderItemRepository;
import com.destiny.orderservice.domain.repository.OrderRepository;
import com.destiny.orderservice.infrastructure.exception.OrderError;
import com.destiny.orderservice.infrastructure.messaging.event.outbound.OrderCreateRequestEvent;
import com.destiny.orderservice.infrastructure.messaging.producer.OrderEventProducer;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest.OrderItemCreateRequest;
import com.destiny.orderservice.presentation.dto.response.OrderDetailResponse;
import com.destiny.orderservice.presentation.dto.response.OrderItemForBrandResponse;
import com.destiny.orderservice.presentation.dto.response.OrderProcessingResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventProducer orderEventProducer;

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

        UUID orderId = orderRepository.createOrder(order).getOrderId();

        // TODO : 사가 오케스트레이션으로 카프카 메시지 발송
        OrderCreateRequestEvent event = OrderCreateRequestEvent.from(order);
        orderEventProducer.send(event);

        return orderId;
    }

    @Transactional(readOnly = true)
    public List<OrderItemForBrandResponse> getItemsForBrand(UUID brandId) {

        List<OrderItem> items = orderItemRepository.findByBrandId(brandId);

        return items.stream()
            .map(OrderItemForBrandResponse::from)
            .toList();
    }

    @Transactional
    public UUID cancelOrder(UUID orderId) {

        Order order = getOrder(orderId);

        boolean isAllPending = order.getItems().stream()
            .allMatch(item -> item.getStatus() == OrderItemStatus.PENDING);

        if (!isAllPending) {
            throw new BizException(OrderError.ORDER_CANCEL_NOT_ALLOWED);
        }

        order.getItems().forEach(item -> item.updateStatus(OrderItemStatus.CANCELED));
        order.updateStatus(OrderStatus.CANCELED);

        Order updateOrder = orderRepository.updateOrder(order);

        return updateOrder.getOrderId();
    }

    @Transactional(readOnly = true)
    public Object getOrderDetail(UUID orderId) {

        Order order = getOrder(orderId);

        // 사가 처리 전 상태 : PENDING
        if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
            return OrderProcessingResponse.of(order.getOrderId(), order.getOrderStatus());
        }

        return OrderDetailResponse.fromEntity(order);
    }

    public void deleteOrder(UUID orderId) {
        Order order = getOrder(orderId);

        if (order.getDeletedAt() != null || order.getDeletedBy() != null) {
            order.markDeleted(1L);
        }
    }

    private Order getOrder(UUID orderId) {

        return orderRepository.findOrderWithItems(orderId).orElseThrow(
            () -> new BizException(OrderError.ORDER_NOT_FOUND)
        );
    }
}
