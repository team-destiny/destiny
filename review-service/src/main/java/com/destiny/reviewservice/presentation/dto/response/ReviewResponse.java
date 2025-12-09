package com.destiny.reviewservice.presentation.dto.response;

import com.destiny.reviewservice.domain.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse (
    UUID reviewId,
    UUID userId,
    UUID productId,
    UUID orderId,
    int rating,
    String content,
    String imageUrls,
    LocalDateTime createdAt,
    UUID createdBy,
    LocalDateTime updatedAt,
    UUID updatedBy
){

    public static ReviewResponse of(Review review) {
        return new ReviewResponse(review.getReviewId()
            , review.getUserId()
            , review.getProductId()
            , review.getOrderId()
            , review.getRating()
            , review.getContent()
            , review.getImageUrls()
            , review.getCreatedAt()
            , review.getCreatedBy()
            , review.getUpdatedAt()
            , review.getUpdatedBy()
        );
    }

}
