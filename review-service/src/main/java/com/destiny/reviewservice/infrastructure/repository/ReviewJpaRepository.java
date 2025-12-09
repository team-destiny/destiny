package com.destiny.reviewservice.infrastructure.repository;

import com.destiny.reviewservice.domain.entity.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByReviewIdAndDeletedAtIsNull(UUID reviewId);
    Page<Review> findByProductIdAndDeletedAtIsNull(UUID productId, Pageable pageable);
    Page<Review> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);

}
