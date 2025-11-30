package com.example.reviewservice.presentation.dto.request;


public record ReviewCreateRequest (
    String userId,
    String productId,
    String orderId,
    int rating,
    String content,
    String imageUrls
){

}
