package com.example.reviewservice.domain.repository;

import com.example.reviewservice.domain.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository {
    Review save(Review review);
    Review findById(UUID reviewId);
    Review findByReviewIdAndDeletedAtIsNull(UUID reviewId);
    Page<Review> findByProductIdAndDeletedAtIsNull(UUID productId, Pageable pageable);
    Page<Review> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);
}
