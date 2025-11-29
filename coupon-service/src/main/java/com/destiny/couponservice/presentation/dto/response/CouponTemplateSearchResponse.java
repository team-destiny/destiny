package com.destiny.couponservice.presentation.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class CouponTemplateSearchResponse {

    private List<CouponTemplateGetResponse> contents;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static CouponTemplateSearchResponse from(Page<CouponTemplateGetResponse> page) {
        return CouponTemplateSearchResponse.builder()
            .contents(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
