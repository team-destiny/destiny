package com.destiny.brandservice.infrastructure.client;

import com.destiny.brandservice.infrastructure.config.FeignAuthConfig;
import com.destiny.brandservice.presentation.dto.response.OrderForBrandResponse;
import com.destiny.brandservice.presentation.dto.response.OrderItemForBrandResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", configuration = FeignAuthConfig.class)
public interface OrderClient {

    @GetMapping("/internal/v1/orders/items")
    List<OrderForBrandResponse> getItemsForBrand(@RequestParam UUID brandId);
}
