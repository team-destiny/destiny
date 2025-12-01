package com.destiny.productservice.presentation.controller;

import com.destiny.productservice.application.service.ProductCommandService;
import com.destiny.productservice.application.service.ProductQueryService;
import com.destiny.productservice.application.query.SearchProductQuery;
import com.destiny.productservice.presentation.dto.request.CreateProductRequest;
import com.destiny.productservice.presentation.dto.request.UpdateProductRequest;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductCommandService productCommandService;

    private final ProductQueryService productQueryService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProduct(
        @RequestParam("size") String size,
        @RequestParam("color") String color) {

        List<ProductResponse> responses = productQueryService
            .getProducts(new SearchProductQuery(size, color));

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable("productId") UUID productId) {

        ProductResponse response = productQueryService.getProduct(productId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(
        @RequestBody CreateProductRequest request) {

        productCommandService.createProduct(request.toCommand());

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateProduct(
        @RequestBody UpdateProductRequest request) {

        productCommandService.updateProduct(request.toCommand());

        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Long id) {
        return null;
    }
}
