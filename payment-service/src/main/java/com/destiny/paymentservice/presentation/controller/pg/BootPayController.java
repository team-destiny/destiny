package com.destiny.paymentservice.presentation.controller.pg;

import com.destiny.global.response.ApiResponse;
import com.destiny.paymentservice.application.service.impl.pg.BootPayServiceImpl;
import com.destiny.paymentservice.infrastructure.config.BootPayProperties;
import com.destiny.paymentservice.presentation.code.PaymentSuccessCode;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
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
@RequestMapping("/v1/payments/bootpay")
@RequiredArgsConstructor
public class BootPayController {

    private final BootPayServiceImpl bootPayService;
    private final BootPayProperties bootPayProperties;

    /**
     * [View] 부트페이 결제 페이지 진입
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        // 프론트 SDK에 필요한 Application ID 전달
        log.info("Bootpay App ID: {}", bootPayProperties.getApplicationId());
        model.addAttribute("applicationId", bootPayProperties.getApplicationId());
        return "bootpay/checkout";
    }

    /**
     * [API] 결제 승인 요청 (브라우저에서 호출)
     */
    @PostMapping("/confirm")
    @ResponseBody
    public PaymentResponse confirm(@RequestBody PaymentConfirmRequest request) {
        return bootPayService.confirmPayment(request);
    }

    /**
     * [View] 결제 완료 후 이동할 결과 페이지 (선택 사항)
     */
    @GetMapping("/success")
    public String successPage(@RequestParam String receipt_id, @RequestParam String orderId, Model model) {
        model.addAttribute("receiptId", receipt_id);
        model.addAttribute("orderId", orderId);
        return "bootpay/success";
    }
    /**
     * [API] 결제 취소 요청
     */
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> cancel(@Valid @RequestBody PaymentCancelRequest request) {
        PaymentResponse response = bootPayService.cancelPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CANCEL_SUCCESS, response));
    }
}