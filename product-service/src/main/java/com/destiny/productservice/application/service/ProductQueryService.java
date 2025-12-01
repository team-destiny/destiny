package com.destiny.productservice.application.service;

import com.destiny.productservice.application.query.SearchProductQuery;
import com.destiny.productservice.presentation.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {

    public ProductResponse getProduct(UUID productId) {
        return null;
    }

    public List<ProductResponse> getProducts(SearchProductQuery searchProductQuery) {
        return null;
    }
}
