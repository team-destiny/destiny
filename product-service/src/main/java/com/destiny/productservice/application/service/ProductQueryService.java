package com.destiny.productservice.application.service;

import com.destiny.productservice.domain.entity.ProductView;
import com.destiny.productservice.domain.repository.ProductQueryRepository;
import com.destiny.productservice.presentation.dto.request.ProductSearch;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {

        ProductView productView = productQueryRepository.findById(productId)
            .orElseThrow();

        return ProductResponse.of(productView);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(ProductSearch search, Pageable pageable) {

        Page<ProductView> result = productQueryRepository.search(search, pageable);

        return result.map(ProductResponse::of);
    }
}
