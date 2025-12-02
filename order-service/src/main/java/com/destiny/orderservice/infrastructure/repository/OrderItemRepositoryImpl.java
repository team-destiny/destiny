package com.destiny.orderservice.infrastructure.repository;

import com.destiny.orderservice.domain.entity.OrderItem;
import com.destiny.orderservice.domain.repository.OrderItemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public List<OrderItem> findByBrandId(UUID brandId) {

        return orderItemJpaRepository.findByBrandId(brandId);
    }
}
