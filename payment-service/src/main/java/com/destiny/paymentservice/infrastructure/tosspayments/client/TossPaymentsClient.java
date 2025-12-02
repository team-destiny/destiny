package com.destiny.paymentservice.infrastructure.tosspayments.client;

import com.destiny.paymentservice.infrastructure.tosspayments.dto.request.TossConfirmRequest;
import com.destiny.paymentservice.infrastructure.tosspayments.dto.response.TossConfirmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "tossPayments",
    url = "https://api.tosspayments.com",
    configuration = TossClientConfig.class
)
public interface TossPaymentsClient {

    // TOSS 결제 승인 API: POST /v1/payments/confirm
    // Feign이 TossConfirmRequest 객체를 JSON으로 변환하여 요청하고, 응답 JSON을 TossConfirmResponse로 자동 변환합니다.
    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirmPayment(@RequestBody TossConfirmRequest request);

    // TODO: 결제 취소 API, 결제 조회 API 등 추가
    // @PostMapping("/v1/payments/{paymentKey}/cancel")
    // TossCancelResponse cancelPayment(@PathVariable String paymentKey, ...);
}