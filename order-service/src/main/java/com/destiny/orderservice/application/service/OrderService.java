package com.destiny.orderservice.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.entity.OrderItemStatus;
import com.destiny.orderservice.domain.entity.OrderStatus;
import com.destiny.orderservice.domain.repository.OrderItemRepository;
import com.destiny.orderservice.domain.repository.OrderRepository;
import com.destiny.orderservice.infrastructure.auth.CustomUserDetails;
import com.destiny.orderservice.infrastructure.exception.OrderError;
import com.destiny.orderservice.infrastructure.messaging.event.outbound.OrderCreateRequestEvent;
import com.destiny.orderservice.infrastructure.messaging.event.result.OrderCreateFailedEvent;
import com.destiny.orderservice.infrastructure.messaging.producer.OrderProducer;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest.OrderItemCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderStatusRequest;
import com.destiny.orderservice.presentation.dto.response.OrderDetailResponse;
import com.destiny.orderservice.presentation.dto.response.OrderItemForBrandResponse;
import com.destiny.orderservice.presentation.dto.response.OrderListResponse;
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
    private final OrderProducer orderProducer;

    @Transactional
    public UUID createOrder(CustomUserDetails customUserDetails, OrderCreateRequest req) {

        Order order = Order.of(
            customUserDetails.getUserId(),
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

        OrderCreateRequestEvent event = OrderCreateRequestEvent.from(order, req.cartId());
        orderProducer.send(event);

        return orderId;
    }

    @Transactional(readOnly = true)
    public List<OrderListResponse> getOrderList(CustomUserDetails customUserDetails) {

        if (customUserDetails.getUserRole().equalsIgnoreCase("MASTER")) {
            List<Order> orders = orderRepository.findAll();

            return orders.stream()
                .map(OrderListResponse::from)
                .toList();
        }

        List<Order> orders = orderRepository.findAllByUserId(customUserDetails.getUserId());

        return orders.stream()
            .map(OrderListResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderItemForBrandResponse> getItemsForBrand(UUID brandId) {

        List<OrderItem> items = orderItemRepository.findByBrandId(brandId);

        return items.stream()
            .map(OrderItemForBrandResponse::from)
            .toList();
    }

    @Transactional
    public UUID cancelOrder(CustomUserDetails customUserDetails, UUID orderId) {

        Order order = getOrder(orderId);

        validateOrderUser(order, customUserDetails.getUserId());

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
    public Object getOrderDetail(CustomUserDetails customUserDetails, UUID orderId) {
        System.out.println(customUserDetails.getUserId());
        System.out.println(customUserDetails.getUserRole());

        Order order = getOrder(orderId);

        if (!customUserDetails.getUserRole().equalsIgnoreCase("MASTER")) {
            validateOrderUser(order, customUserDetails.getUserId());
        }

        if (order.getDeletedAt() != null || order.getDeletedBy() != null) {
            throw new BizException(OrderError.ORDER_NOT_FOUND);
        }

        // 사가 처리 전 상태 : PENDING
        if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
            return OrderProcessingResponse.of(order.getOrderId(), order.getOrderStatus());
        }

        // 주문 요청 실패 : FAILED (존재하지 않는 상품, 재고 수량 부족, 결제 실패)
        if (order.getOrderStatus().equals(OrderStatus.FAILED)) {
            return OrderProcessingResponse.of(order.getOrderId(), order.getOrderStatus());
        }

        return OrderDetailResponse.fromEntity(order);
    }

    @Transactional
    public void deleteOrder(CustomUserDetails customUserDetails, UUID orderId) {
        Order order = getOrder(orderId);

        if (order.getDeletedAt() != null || order.getDeletedBy() != null) {
            throw new BizException(OrderError.ORDER_NOT_FOUND);
        }

        validateOrderUser(order, customUserDetails.getUserId());

        order.markDeleted(customUserDetails.getUserId());
    }

    @Transactional
    public UUID changeOrderStatus(CustomUserDetails customUserDetails, OrderStatusRequest req, UUID orderId) {

        if (!customUserDetails.getUserRole().equalsIgnoreCase("MASTER")) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }

        Order order = getOrder(orderId);

        order.updateStatus(req.orderStatus());
        Order updateOrder = orderRepository.updateOrder(order);

        return updateOrder.getOrderId();
    }

    @Transactional
    public void failOrder(OrderCreateFailedEvent event) {
        Order order = getOrder(event.orderId());

        order.updateStatus(OrderStatus.FAILED);

    }

    private Order getOrder(UUID orderId) {

        return orderRepository.findOrderWithItems(orderId).orElseThrow(
            () -> new BizException(OrderError.ORDER_NOT_FOUND)
        );
    }

    private void validateOrderUser(Order order, UUID userId) {
        if (!order.getUserId().equals(userId)) {
            throw new BizException(OrderError.ORDER_NOT_FOUND);
        }
    }
}
