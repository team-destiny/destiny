package com.destiny.orderservice.infrastructure.repository;

import com.destiny.orderservice.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query("""
           select o from Order o
           left join fetch o.items
           where o.orderId = :orderId
        """)
    Optional<Order> findOrderWithItems(UUID orderId);
}
