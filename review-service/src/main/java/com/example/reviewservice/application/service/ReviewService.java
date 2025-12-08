package com.example.reviewservice.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.example.reviewservice.domain.entity.Review;
import com.example.reviewservice.domain.entity.UserRole;
import com.example.reviewservice.domain.repository.ReviewRepository;
import com.example.reviewservice.infrastructure.security.auth.CustomUserDetails;
import com.example.reviewservice.presentation.dto.request.ReviewCreateRequest;
import com.example.reviewservice.presentation.dto.request.ReviewUpdateRequest;
import com.example.reviewservice.presentation.dto.response.ReviewResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final PageableArgumentResolver pageableArgumentResolver;
    private final ReviewRepository reviewRepository;
    private final OrderClientService orderClientService;

    @Transactional
    public ReviewResponse createReview(CustomUserDetails userDetails, ReviewCreateRequest reviewCreateRequest) {
        validateAccess(userDetails.getUserId(), UserRole.valueOf(userDetails.getUserRole()), reviewCreateRequest.userId());
        orderClientService.verifyUserCanReview(
            userDetails.getAccessJwt(),
            userDetails.getUserId(),
            reviewCreateRequest.orderId(),
            reviewCreateRequest.productId());

        Review review = Review.createReview(
            reviewCreateRequest.userId(),
            reviewCreateRequest.productId(),
            reviewCreateRequest.orderId(),
            reviewCreateRequest.rating(),
            reviewCreateRequest.content(),
            reviewCreateRequest.imageUrls()
        );

        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.of(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(
        UUID userId, UUID reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        Review review = reviewRepository.findById(reviewId);
        validateAccess(userId, null, review.getUserId());
        if(!userId.equals(review.getUserId())) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }

        review.updateReview(reviewUpdateRequest.rating(),  reviewUpdateRequest.content(), reviewUpdateRequest.imageUrls());
        return ReviewResponse.of(review);
    }

    @Transactional
    public void deleteReview(UUID userId, UserRole userRole, UUID reviewId) {
        Review review = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId);
        validateAccess(userId, userRole, review.getUserId());
        review.markDeleted(reviewId);
    }

    public Page<ReviewResponse> getByProduct(UUID productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductIdAndDeletedAtIsNull(productId, pageable);
        return reviews.map(review -> ReviewResponse.of(review));
    }

    public Page<ReviewResponse> getByUser(UUID authUserId, UserRole authUserRole, UUID userId, Pageable pageable) {
        validateAccess(authUserId, authUserRole, userId);
        Page<Review> reviews = reviewRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
        return reviews.map(review -> ReviewResponse.of(review));
    }

    private void validateAccess(UUID authUserId, UserRole authUserRole, UUID targetUserId) {
        // 본인 or ADMIN 만 허용
        if (!authUserId.equals(targetUserId) && authUserRole != UserRole.MASTER) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }
}
