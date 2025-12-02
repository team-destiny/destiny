package com.destiny.orderservice.domain.repository;

import com.destiny.orderservice.domain.entity.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository {

    List<OrderItem> findByBrandId(UUID brandId);
}
