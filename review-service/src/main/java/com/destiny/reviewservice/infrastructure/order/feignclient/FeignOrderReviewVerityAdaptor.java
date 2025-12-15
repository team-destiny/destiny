package com.destiny.reviewservice.infrastructure.order.feignclient;

import com.destiny.global.exception.BizException;
import com.destiny.reviewservice.application.port.out.OrderReviewVerityPort;
import com.destiny.reviewservice.infrastructure.order.feignclient.OrderFeignClient.OrderDetailResponse;
import com.destiny.reviewservice.infrastructure.order.feignclient.OrderFeignClient.OrderItemResponse;
import com.destiny.reviewservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.reviewservice.presentation.advice.ReviewErrorCode;
import com.destiny.reviewservice.presentation.dto.request.ReviewCreateRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeignOrderReviewVerityAdaptor implements OrderReviewVerityPort {

    private final OrderFeignClient orderFeignClient;

    @Override
    public void verifyUserCanReview(
        CustomUserDetails userDetails,
        ReviewCreateRequest reviewCreateRequest) {

        String accessToken = "Bearer " + userDetails.getAccessJwt();
        UUID authUserId = userDetails.getUserId();
        UUID orderId = reviewCreateRequest.orderId();
        UUID productId = reviewCreateRequest.productId();

        OrderDetailResponse order =
            orderFeignClient.getOrderDetail("Bearer " + accessToken, orderId);

        if(order == null) {
            throw new BizException(ReviewErrorCode.ORDER_INFO_NOT_FOUND);
        }

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
