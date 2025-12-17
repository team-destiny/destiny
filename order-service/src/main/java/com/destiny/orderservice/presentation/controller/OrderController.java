package com.destiny.orderservice.presentation.controller;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.infrastructure.auth.CustomUserDetails;
import com.destiny.orderservice.presentation.dto.request.OrderCancelRequest;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import com.destiny.orderservice.presentation.dto.request.OrderStatusRequest;
import com.destiny.orderservice.presentation.dto.response.OrderListResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<UUID> createOrder(
        @RequestBody OrderCreateRequest req,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        UUID order = orderService.createOrder(customUserDetails, req);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderListResponse>> getOrderList(
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(orderService.getOrderList(customUserDetails));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(
        @PathVariable("orderId") UUID orderId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(orderService.getOrderDetail(customUserDetails, orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<UUID> cancelOrder(
        @PathVariable("orderId") UUID orderId,
        @RequestBody OrderCancelRequest req,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UUID order = orderService.cancelOrder(customUserDetails, req,  orderId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(order);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<UUID> changeOrderStatus(
        @PathVariable("orderId") UUID orderId,
        @RequestBody OrderStatusRequest req,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        UUID order = orderService.changeOrderStatus(customUserDetails, req, orderId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(order);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(
        @PathVariable("orderId")  UUID orderId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        orderService.deleteOrder(customUserDetails, orderId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body("해당 주문정보가 삭제되었습니다.");
    }
}
