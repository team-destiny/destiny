package com.example.reviewservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import com.example.reviewservice.application.service.ReviewService;
import com.example.reviewservice.domain.entity.Review;
import com.example.reviewservice.presentation.dto.request.ReviewCreateRequest;
import com.example.reviewservice.presentation.dto.request.ReviewUpdateRequest;
import com.example.reviewservice.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        // 더미 데이터 – 실제로는 DB 저장 후 결과 반환
        Review review = getMockReview(reviewCreateRequest);
        ReviewResponse body = ReviewResponse.of(review);

        return ApiResponse.success(CommonSuccessCode.CREATED, body);
    }

    private Review getMockReview(ReviewCreateRequest reviewCreateRequest) {
        Review review = reviewCreateRequest != null ? Review.createReview(
            reviewCreateRequest.userId(),
            reviewCreateRequest.productId(),
            reviewCreateRequest.orderId(),
            reviewCreateRequest.rating(),
            reviewCreateRequest.content(),
            reviewCreateRequest.imageUrls()
        ) : Review.createReview(
            "dummy-user-id",
            "dummy-product-id",
            "dummy-order-id",
            5,
            "리뷰 내용",
            ""
        );

        return review;
    }

    @PatchMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
        @PathVariable("reviewId") String reviewId,
        @RequestBody ReviewUpdateRequest reviewUpdateRequest
    ) {
        // 더미: 수정된 것처럼 응답만 구성
        Review review = getMockUpdateReview(reviewUpdateRequest);
        ReviewResponse body = ReviewResponse.of(review);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    private Review getMockUpdateReview(ReviewUpdateRequest reviewUpdateRequest) {
        Review review = Review.createReview(
            "dummy-user-id",
            "dummy-product-id",
            "dummy-order-id",
            5,
            "리뷰 내용",
            ""
        );

        review.updateReview(
            reviewUpdateRequest.rating(),
            reviewUpdateRequest.content(),
            reviewUpdateRequest.imageUrls()
        );

        return review;
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
        @PathVariable("reviewId") String reviewId
    ) {
        // 더미: 실제 삭제 없이 204만 응답
        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @GetMapping
    public ApiResponse<List<ReviewResponse>> getReviewsByProduct(
        @RequestParam(required = false) String productId,
        @RequestParam(required = false) String userId
    ) {

        // 더미 리스트
        List<Review> reviews = List.of(
            getMockReview(null),
            getMockReview(null)
        );

        List<ReviewResponse> body = reviews.stream()
            .map(ReviewResponse::of)
            .toList();

        if (productId != null && userId == null) {
            return ApiResponse.success(CommonSuccessCode.OK, body);
//            TODO : return ApiResponse.success(CommonSuccessCode.OK, reviewService.getByProduct(productId));
        }

        if (userId != null && productId == null) {
            return ApiResponse.success(CommonSuccessCode.OK, body);
//            TODO : return ApiResponse.success(CommonSuccessCode.OK, reviewService.getByUser(userId));
        }

        // 둘 다 null → 전체 조회 or 에러
        // 둘 다 not null → 복합 조건 조회 or 에러

        throw new IllegalArgumentException("productId 또는 userId 중 하나는 꼭 있어야 합니다.");
    }


}
