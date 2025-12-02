package com.destiny.orderservice.infrastructure.repository;

import com.destiny.orderservice.domain.entity.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByBrandId(UUID brandId);
}
