package com.destiny.orderservice.presentation.controller;

import com.destiny.orderservice.application.service.OrderService;
import com.destiny.orderservice.presentation.dto.response.OrderForBrandResponse;
import com.destiny.orderservice.presentation.dto.response.OrderItemForBrandResponse;
import com.destiny.orderservice.presentation.dto.response.OrderItemResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/orders")
@RequiredArgsConstructor
public class InternalController {

    private final OrderService orderService;

    @GetMapping("/items")
    public ResponseEntity<List<OrderForBrandResponse>> getItemsForBrand(
        @RequestParam UUID brandId
    ) {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(orderService.getItemsForBrand(brandId));
    }

}
