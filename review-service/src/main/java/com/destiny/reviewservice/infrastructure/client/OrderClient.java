package com.destiny.reviewservice.infrastructure.client;

import com.destiny.global.response.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service", path = "/v1/orders")
public interface OrderClient {

    @GetMapping("/{orderId}")
    ApiResponse<OrderDetailResponse> getOrderDetail(
        @RequestHeader("Authorization") String accessToken,
        @PathVariable("orderId") UUID orderId);

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
