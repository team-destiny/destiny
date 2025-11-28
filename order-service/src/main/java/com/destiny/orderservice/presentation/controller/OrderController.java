package com.destiny.orderservice.presentation.controller;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.presentation.dto.request.OrderCreateRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<UUID> createOrder(@RequestBody OrderCreateRequest req){
        UUID order = orderService.createOrder(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(
        @PathVariable("orderId") UUID orderId
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(orderService.getOrderDetail(orderId));
    }
}
