package com.example.reviewservice.presentation.controller;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.exception.BizException;
import com.destiny.global.response.ApiResponse;
import com.example.reviewservice.application.service.ReviewService;
import com.example.reviewservice.domain.entity.UserRole;
import com.example.reviewservice.infrastructure.security.auth.CustomUserDetails;
import com.example.reviewservice.presentation.advice.PageingUtils;
import com.example.reviewservice.presentation.advice.ReviewErrorCode;
import com.example.reviewservice.presentation.dto.request.ReviewCreateRequest;
import com.example.reviewservice.presentation.dto.request.ReviewUpdateRequest;
import com.example.reviewservice.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(
        @Valid @RequestBody ReviewCreateRequest reviewCreateRequest
    ) {
        ReviewResponse body = reviewService.createReview(reviewCreateRequest);
        return ApiResponse.success(CommonSuccessCode.CREATED, body);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("reviewId") UUID reviewId,
        @RequestBody ReviewUpdateRequest reviewUpdateRequest
    ) {
        UUID userId = userDetails.getUserId();
        ReviewResponse body = reviewService.updateReview(userId, reviewId, reviewUpdateRequest);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("reviewId") UUID reviewId
    ) {
        UUID userId = userDetails.getUserId();
        UserRole userRole = UserRole.valueOf(userDetails.getUserRole());
        reviewService.deleteReview(userId, userRole, reviewId);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @GetMapping
    public ApiResponse<Page<ReviewResponse>> getReviews(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) UUID productId,
        @RequestParam(required = false) UUID userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy, // createdAt / rating
        @RequestParam(defaultValue = "true") boolean isDescending
    ) {
        if(productId != null && userId != null) {
            throw new BizException(ReviewErrorCode.INVALID_SEARCH_CONDITION);
        }

        Pageable pageable = PageingUtils.createPageable(page, size, sortBy, isDescending);

        Page<ReviewResponse> body = null;
        if ((productId == null && userId == null) || (productId != null && userId != null)) {
          body = reviewService.getByProduct(productId, pageable);
        }

        if (userId != null && productId == null) {
            if(userDetails == null) {
                throw new BizException(CommonErrorCode.UNAUTHORIZED);
            }

            UUID authUserId = userDetails.getUserId();
            UserRole authUserRole = UserRole.valueOf(userDetails.getUserRole());
            body = reviewService.getByUser(authUserId, authUserRole, userId, pageable);
        }

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }
}
