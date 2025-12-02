package com.destiny.paymentservice.presentation.controller;

import com.destiny.paymentservice.application.service.PaymentCommandService;
import com.destiny.paymentservice.domain.dto.PgRequestDto;
import com.destiny.paymentservice.domain.dto.PgResponseDto;
import com.destiny.paymentservice.domain.vo.PaymentType;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TossPaymentsConfirmController {

    // Application Service 주입
    private final PaymentCommandService paymentCommandService;

    // 기존 @RequestMapping(value = "/confirm") 대신 POST Mapping 사용
    @PostMapping(value = "/confirm")
    // 클라이언트의 요청 바디를 Presentation DTO로 받습니다.
    public ResponseEntity<PgResponseDto> confirmPayment(@RequestBody PaymentConfirmRequest request) {

        // 1. Presentation DTO를 Domain 표준 PgRequestDto로 변환
        // TODO: customer 정보는 세션 또는 별도 API 호출로 가져와야 합니다. 현재는 임시값 사용.
        PgRequestDto pgRequest = new PgRequestDto(
            request.orderId(),
            request.amount(),
            PaymentType.TOSSPAYMENTS,
            request.paymentKey(),
            "customer@destiny.com",
            "Customer Name",
            "01000000000"
        );

        // 2. Application Service 호출 (PG 통신 및 DB 저장 트랜잭션 수행)
        PgResponseDto response = paymentCommandService.confirmAndSavePayment(pgRequest);

        // 3. 응답 처리 및 반환
        if (response != null ) {
            log.info("결제 승인 및 DB 저장 성공. OrderId: {}", response.orderId());
            // 성공 시 200 OK와 PG사로부터 받은 표준 응답을 반환
            return ResponseEntity.ok(response);
        } else {
            // 실패 시 400 Bad Request와 실패 응답을 반환하여 클라이언트가 /fail 페이지로 리다이렉트하도록 유도
            log.error("결제 실패. Code: {}, Message: {}", response.failCode(), response.failMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
        }
    }
}