package com.destiny.reviewservice.infrastructure.order.feignclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service", path = "/v1/orders")
public interface OrderFeignClient {

    @GetMapping("/{orderId}")
    OrderDetailResponse getOrderDetail(
        @RequestHeader("Authorization") String accessToken,
        @PathVariable("orderId") UUID orderId);

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OrderDetailResponse(
        UUID orderId,
        UUID userId,
        List<OrderItemResponse> items
    ) {}

    record OrderItemResponse(
        UUID productId,
        OrderItemStatus status) {}

    enum OrderItemStatus {

        PENDING,
        OUT_OF_STOCK,
        CANCELED,
        PREPARING,
        SHIPPING,
        DELIVERED;

        public boolean isReviewable() {
            return this == DELIVERED;
        }
    }
}
