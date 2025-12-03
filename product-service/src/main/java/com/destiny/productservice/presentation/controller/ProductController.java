package com.destiny.productservice.presentation.controller;

import com.destiny.productservice.application.dto.ProductSearch;
import com.destiny.productservice.application.service.ProductCommandService;
import com.destiny.productservice.application.service.ProductQueryService;
import com.destiny.productservice.presentation.dto.request.CreateProductRequest;
import com.destiny.productservice.presentation.dto.request.UpdateProductRequest;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<ProductResponse>> getProduct(
        @RequestParam("minPrice") Long minPrice,
        @RequestParam("maxPrice") Long maxPrice,
        @RequestParam("nameContains") String nameContains,
        @RequestParam("brand") UUID brandId,
        @RequestParam("size") String size,
        @RequestParam("color") String color,
        Pageable pageable) {

        Page<ProductResponse> pages = productQueryService.getProducts(
                new ProductSearch(minPrice, maxPrice, nameContains, brandId, size, color),
                pageable
        );

        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable("productId") UUID productId) {

        ProductResponse response = productQueryService.getProductById(productId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @RequestBody CreateProductRequest request) {

        ProductResponse response = productCommandService.createProduct(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
        @PathVariable("productId")  UUID productId,
        @RequestBody UpdateProductRequest request) {

        productCommandService.updateProduct(productId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductById(
        @PathVariable("productId") UUID productId) {

        // TODO 유저 아이디 추가 필요

        productCommandService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
