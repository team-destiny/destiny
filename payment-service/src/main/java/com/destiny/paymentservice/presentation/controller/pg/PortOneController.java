package com.destiny.paymentservice.presentation.controller.pg;

import com.destiny.global.response.ApiResponse;
import com.destiny.paymentservice.application.service.impl.pg.PortOneServiceImpl;
import com.destiny.paymentservice.infrastructure.config.PortOneProperties;
import com.destiny.paymentservice.presentation.code.PaymentSuccessCode;
import com.destiny.paymentservice.presentation.dto.request.pg.portone.PortOneCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.pg.portone.PortOneConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/v1/payments/portone")
@RequiredArgsConstructor
public class PortOneController {

    private final PortOneServiceImpl portOneService;
    private final PortOneProperties portOneProperties;

    /**
     * 1. 포트원 결제 체크아웃 페이지 이동
     * GET /v1/payments/portone/checkout
     */
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        // HTML에 상점 ID와 채널 키를 전달합니다.
        model.addAttribute("storeId", portOneProperties.getStoreId());
//        model.addAttribute("channelKey", portOneProperties.getChannelKey());
        model.addAttribute("channelGroupId", portOneProperties.getChannelGroupId());
        return "/portone/checkout";
    }

    /**
     * 2. 결제 승인 및 검증 API
     * POST /v1/payments/portone/confirm
     */
    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> confirm(@Valid @RequestBody PortOneConfirmRequest request) {
        PaymentResponse response = portOneService.confirmPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CONFIRM_SUCCESS, response));
    }

    /**
     * 3. 결제 취소 API
     * POST /v1/payments/portone/cancel
     */
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> cancel(@Valid @RequestBody PortOneCancelRequest request) {
        PaymentResponse response = portOneService.cancelPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CANCEL_SUCCESS, response));
    }
}