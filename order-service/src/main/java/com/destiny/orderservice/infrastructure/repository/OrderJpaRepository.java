package com.destiny.orderservice.infrastructure.repository;

import com.destiny.orderservice.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

}
