package com.destiny.reviewservice.application.port.out;

import com.destiny.reviewservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.reviewservice.presentation.dto.request.ReviewCreateRequest;

public interface OrderReviewVerityPort {
    void verifyUserCanReview(CustomUserDetails userDetails, ReviewCreateRequest reviewCreateRequest);
}
