package com.destiny.cartservice.infrastructure.client;

import com.destiny.cartservice.infrastructure.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/v1/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") UUID productId);
}