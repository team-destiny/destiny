package com.destiny.reviewservice.presentation.dto.request;

public record ReviewUpdateRequest(
    int rating,
    String content,
    String imageUrls
) {

}
