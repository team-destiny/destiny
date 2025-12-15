package com.destiny.paymentservice.presentation.controller.pg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/v1/payments/tosspayments")
@RequiredArgsConstructor
public class TossPaymentsController {

    // TODO: 결제 위젯 페이지 렌더링을 위해 @Value 대신 Service 또는 Config로 관리 필요
    @Value("${payment.toss.client-key:test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm}")
    private String TOSS_CLIENT_KEY;

    // =======================================================
    // 1. View Controller 기능 (Thymeleaf 페이지 렌더링)
    // =======================================================

    /**
     * GET /payments/tosspayments/checkout
     *
     * @param orderId 주문 ID (선택적 파라미터)
     * @param model   Thymeleaf Model
     */
    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam(required = false) String orderId, Model model) {
        // TODO: 실제로는 OrderService에서 주문 조회 및 금액/이름/고객 정보 조회 로직 필요

        // 하드코딩된 mock 데이터
        String finalOrderId = (orderId != null) ? orderId : "ORDER-" + System.currentTimeMillis();

        // Thymeleaf로 전달할 동적 데이터
        model.addAttribute("clientKey", TOSS_CLIENT_KEY);
        model.addAttribute("orderId", finalOrderId);
        model.addAttribute("customerKey", "CUSTOMER-" + System.currentTimeMillis()); // 실제로는 사용자 ID 기반 생성
        model.addAttribute("amount", 50000); // Integer로 통일
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

}