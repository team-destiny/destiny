package com.destiny.paymentservice.infrastructure.feign;

import com.destiny.paymentservice.presentation.dto.request.pg.tosspayments.TossPaymentsConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.pg.TossPaymentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "toss-payments", url = "https://api.tosspayments.com")
public interface TossPaymentsClient {

    @PostMapping("/v1/payments/confirm")
    TossPaymentsResponse confirm(@RequestHeader("Authorization") String authorization, @RequestBody TossPaymentsConfirmRequest request);
}