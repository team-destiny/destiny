package com.destiny.couponservice.presentation.dto.response;


import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class IssuedCouponSearchResponse {

    private List<IssuedCouponListItemResponse> contents;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static IssuedCouponSearchResponse from(Page<IssuedCouponListItemResponse> page) {
        return IssuedCouponSearchResponse.builder()
            .contents(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
