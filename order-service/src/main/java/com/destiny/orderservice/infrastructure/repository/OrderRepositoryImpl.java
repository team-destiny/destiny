package com.destiny.orderservice.infrastructure.repository;

import com.destiny.orderservice.domain.entity.Order;
import com.destiny.orderservice.domain.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order createOrder(Order order) {

        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findOrderWithItems(UUID orderId) {

        return orderJpaRepository.findOrderWithItems(orderId);
    }

    @Override
    public Order updateOrder(Order order) {

        return orderJpaRepository.save(order);
    }
}
