package com.example.reviewservice.presentation.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest (
    @NotBlank(message = "사용자 ID는 필수입니다")
    String userId,
    @NotBlank(message = "상품 ID는 필수입니다")
    String productId,
    @NotBlank(message = "주문 ID는 필수입니다")
    String orderId,
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    int rating,
    @NotBlank(message = "리뷰 내용은 필수입니다")
    String content,
    String imageUrls
){

}
