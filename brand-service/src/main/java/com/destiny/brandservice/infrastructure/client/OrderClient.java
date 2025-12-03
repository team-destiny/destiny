package com.destiny.brandservice.infrastructure.client;

import com.destiny.brandservice.presentation.dto.response.OrderItemForBrandResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/internal/v1/orders/items")
    List<OrderItemForBrandResponse> getItemsForBrand(@RequestParam UUID brandId);
}
