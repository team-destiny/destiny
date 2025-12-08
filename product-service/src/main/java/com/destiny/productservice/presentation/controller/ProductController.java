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
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductCommandService productCommandService;

    private final ProductQueryService productQueryService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProduct(
        @RequestParam(value = "minPrice", required = false) Integer minPrice,
        @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
        @RequestParam(value = "nameContains", required = false) String nameContains,
        @RequestParam(value = "brand", required = false) UUID brandId,
        @RequestParam(value = "size", required = false) String size,
        @RequestParam(value = "color", required = false) String color,
        Pageable pageable) {

        Page<ProductResponse> pages = productQueryService.getProducts(
                ProductSearch.of(minPrice, maxPrice, nameContains, brandId, size, color),
                pageable
        );

        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{brandId}/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable UUID brandId,
        @PathVariable UUID productId) {

        ProductResponse response = productQueryService
            .getProductByBrandIdAndId(brandId, productId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{brandId}")
    public ResponseEntity<ProductResponse> createProduct(
        @PathVariable UUID brandId,
        @RequestBody CreateProductRequest request) {

        ProductResponse response = productCommandService
            .createProduct(brandId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{brandId}/{productId}")
    public ResponseEntity<Void> updateProduct(
        @PathVariable UUID brandId,
        @PathVariable UUID productId,
        @RequestBody UpdateProductRequest request) {

        productCommandService.updateProduct(brandId, productId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{brandId}/{productId}")
    public ResponseEntity<Void> deleteProductById(
        @PathVariable UUID brandId,
        @PathVariable UUID productId) {

        // TODO 유저 아이디 추가 필요

        productCommandService.deleteProduct(brandId, productId);

        return ResponseEntity.noContent().build();
    }
}
