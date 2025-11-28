package com.destiny.orderservice.domain.repository;

import com.destiny.orderservice.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order createOrder(Order order);

    Optional<Order> findOrderWithItems(UUID orderId);
}
