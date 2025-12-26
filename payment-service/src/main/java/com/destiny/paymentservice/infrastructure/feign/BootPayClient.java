package com.destiny.paymentservice.infrastructure.feign;

import com.destiny.paymentservice.presentation.dto.response.pg.BootPayReceiptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "bootpay-client", url = "https://api.bootpay.co.kr")
public interface BootPayClient {

    @PostMapping(value = "/v2/request/token", consumes = "application/json")
    BootPayTokenResponse getAccessToken(@RequestBody BootPayTokenRequest request);

    // [중요] 서버 승인 API 호출 (영수증 조회 GET이 아니라 POST confirm입니다)
    @PostMapping(value = "/v2/confirm", consumes = "application/json")
    BootPayReceiptResponse confirmPayment(
        @RequestHeader("Authorization") String token,
        @RequestHeader("Accept") String accept,
        @RequestBody BootPayConfirmPayload payload
    );

    // BootPayClient.java 인터페이스 내부에 추가
    @PostMapping(value = "/v2/cancel", consumes = "application/json")
    BootPayReceiptResponse cancelPayment(
        @RequestHeader("Authorization") String token,
        @RequestBody BootPayCancelPayload payload
    );
}