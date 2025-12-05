package com.destiny.paymentservice.presentation.controller;

import com.destiny.paymentservice.domain.vo.PaymentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentViewController {

    // TODO: MainPG를 읽어오는 Service 또는 Configuration 주입이 필요
    // private final PaymentConfigService paymentConfigService;

    /**
     * [1번 요청] 메인 결제사 설정에 따라 해당 PG사의 결제 페이지로 라우팅합니다. GET /payments/checkout
     *
     * @param orderId 주문 ID (필수 아님)
     * @return 해당 PG사의 checkout 페이지로 리다이렉트
     */
    @GetMapping("/checkout")
    public String routeToMainPgCheckout(@RequestParam(required = false) String orderId,
        Model model) {

        // 1. MainPG 타입 조회 로직 (현재는 TOSSPAYMENTS로 임시 하드코딩)
        // 실제로는 paymentConfigService.getMainPgType() 등을 통해 DB/Redis에서 읽어와야 합니다.
        PaymentType mainPgType = PaymentType.TOSSPAYMENTS;

        log.info("Main PG Type: {} - Routing to checkout page.", mainPgType);

        // 2. PG사 타입에 따른 경로 생성 (예: TOSSPAYMENTS -> tosspayments)
        String pgPath = "/" + mainPgType.name().toLowerCase();

        // 3. 쿼리 파라미터를 유지하면서 해당 PG사의 전용 checkout 페이지로 리다이렉트 경로 생성
        String redirectUrl = "/payments" + pgPath + "/checkout";
        if (orderId != null && !orderId.isEmpty()) {
            redirectUrl += "?orderId=" + orderId;
        }

        // 예시 리턴 값: "redirect:/tosspayments/checkout?orderId=ORDER-1234"
        return "redirect:" + redirectUrl;
    }
}