package com.destiny.paymentservice.presentation.controller.pg;

import com.destiny.global.response.ApiResponse;
import com.destiny.paymentservice.application.service.impl.pg.TossPaymentsServiceImpl;
import com.destiny.paymentservice.presentation.code.PaymentSuccessCode;
import com.destiny.paymentservice.presentation.dto.request.pg.tosspayments.TossPaymentsConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/v1/payments/tosspayments")
@RequiredArgsConstructor
public class TossPaymentsController {

    @Value("${payment.tosspayments.client-key}")
    private String TOSSPAYMENTS_CLIENT_KEY;

    private final TossPaymentsServiceImpl tossPaymentsService;

    // =======================================================
    // 1. View Controller 기능 (Thymeleaf 페이지 렌더링)
    // =======================================================

    /**
     * GET /payments/tosspayments/checkout
     *
     * @param model   Thymeleaf Model
     */
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        model.addAttribute("clientKey", TOSSPAYMENTS_CLIENT_KEY);
        return "/tosspayments/checkout"; // Thymeleaf 템플릿 경로
    }

    /**
     * 결제 성공 시 이동하는 페이지
     * GET /payments/tosspayments/success
     */
    @GetMapping("/success")
    public String successPage() {
        return "/tosspayments/success";
    }

    /**
     * 결제 실패 시 이동하는 페이지
     * GET /payments/tosspayments/fail
     */
    @GetMapping("/fail")
    public String failPage() {
        return "/tosspayments/fail";
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirm(@Valid @RequestBody TossPaymentsConfirmRequest request) {
        PaymentResponse response = tossPaymentsService.confirmPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CONFIRM_SUCCESS, response));
    }
}