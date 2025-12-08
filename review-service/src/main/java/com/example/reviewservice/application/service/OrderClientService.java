package com.example.reviewservice.application.service;

import com.destiny.global.exception.BizException;
import com.destiny.global.response.ApiResponse;
import com.example.reviewservice.infrastructure.client.OrderClient;
import com.example.reviewservice.infrastructure.client.OrderClient.OrderDetailResponse;
import com.example.reviewservice.infrastructure.client.OrderClient.OrderItemResponse;
import com.example.reviewservice.presentation.advice.ReviewErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderClientService {

    private final OrderClient orderClient;

    public void verifyUserCanReview(
        String accessToken,
        UUID authUserId,
        UUID orderId,
        UUID productId
    ) {
        ApiResponse<OrderDetailResponse> response =
            orderClient.getOrderDetail(accessToken, orderId);

        OrderDetailResponse order = response.getData();

        // 주문자 == 현재 로그인 유저인지 확인
        if (!order.userId().equals(authUserId)) {
            throw new BizException(ReviewErrorCode.USER_NOT_ORDER_OWNER);
        }

        // 주문 안에 해당 상품이 포함되어 있는지 확인
        OrderItemResponse item = order.items().stream()
            .filter(i -> i.productId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new BizException(ReviewErrorCode.PRODUCT_NOT_IN_ORDER));

        // 주문 상태가 리뷰 가능한 상태인지 확인 (배송 완료)
        if (!item.status().isReviewable()) {
            throw new BizException(ReviewErrorCode.ORDER_NOT_REVIEWABLE);
        }
    }

}
