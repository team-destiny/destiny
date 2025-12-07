package com.example.reviewservice.infrastructure.repository;

import com.destiny.global.exception.BizException;
import com.example.reviewservice.domain.entity.Review;
import com.example.reviewservice.domain.repository.ReviewRepository;
import com.example.reviewservice.presentation.advice.ReviewErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewJpaRepositoryAdaptor implements ReviewRepository {
    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Review findById(UUID reviewId) {
        return reviewJpaRepository.findById(reviewId)
            .orElseThrow(() -> new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Override
    public Review findByReviewIdAndDeletedAtIsNull(UUID reviewId) {
        return reviewJpaRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
            .orElseThrow(() -> new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Override
    public Page<Review> findByProductIdAndDeletedAtIsNull(UUID productId, Pageable pageable) {
        return reviewJpaRepository.findByProductIdAndDeletedAtIsNull(productId, pageable);
    }

    @Override
    public Page<Review> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable) {
        return reviewJpaRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
    }
}
