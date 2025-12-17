package com.destiny.paymentservice.infrastructure.feign;

import com.destiny.paymentservice.presentation.dto.request.pg.portone.PortOneCancelRequest;
import com.destiny.paymentservice.presentation.dto.response.pg.PortOneResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "portone-client", url = "https://api.portone.io")
public interface PortOneClient {

    /**
     * 포트원 결제 단건 조회 (V2)
     * @param authorization "PortOne {API_SECRET}"
     * @param paymentId 포트원 결제 고유 ID
     */
    @GetMapping("/payments/{paymentId}")
    PortOneResponse getPayment(@RequestHeader("Authorization") String authorization, @PathVariable("paymentId") String paymentId);

    @PostMapping("/payments/{paymentId}/cancel")
    PortOneResponse cancelPayment(@RequestHeader("Authorization") String authorization, @PathVariable("paymentId") String paymentId, @RequestBody PortOneCancelRequest request);
}