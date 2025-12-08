package com.destiny.reviewservice.presentation.dto.response;

import com.destiny.reviewservice.domain.entity.Review;
import java.time.LocalDateTime;

public record ReviewResponse (
    String reviewId,
    String userId,
    String productId,
    String orderId,
    int rating,
    String content,
    String imageUrls,
    LocalDateTime createdAt,
    Long createdBy,
    LocalDateTime updatedAt,
    Long updatedBy
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
