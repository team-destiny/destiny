package com.example.reviewservice.domain.entity;

import com.destiny.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Entity
@Table(name = "p_review")
@Getter
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reviewId;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String productId;
    @Column(nullable = false)
    private String orderId;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating; // (1~5)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String imageUrls;

    public Review() {}

    public static Review createReview(String userId, String productId, String orderId, int rating,
        String content, String imageUrls){
        Review review = new Review();

        review.userId = userId;
        review.productId = productId;
        review.orderId = orderId;
        review.rating = rating;
        review.content = content;
        review.imageUrls = imageUrls;

        return review;
    }

    public void updateReview(Integer rating, String content, String imageUrls) {
        if (rating != null) this.rating = rating;
        if (content != null) this.content = content;
        if (imageUrls != null) this.imageUrls = imageUrls;
    }
}
