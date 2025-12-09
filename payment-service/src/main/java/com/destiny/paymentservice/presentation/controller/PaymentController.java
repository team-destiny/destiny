package com.destiny.paymentservice.presentation.controller;

import com.destiny.global.response.ApiResponse;
import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.application.service.PaymentService;
import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCommand;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.code.PaymentSuccessCode;
import com.destiny.paymentservice.presentation.dto.request.PaymentCancelRequest;
import com.destiny.paymentservice.presentation.dto.request.PaymentConfirmRequest;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /payments/request : 결제 요청 생성 (PENDING 상태)
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<PaymentResponse>> requestPayment(@Valid @RequestBody PaymentCommand request) {
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_REQUEST_SUCCESS, response));
    }

    /**
     * POST /payments/confirm : 결제 승인 처리 (PENDING -> PAID)
     * 결제 Key, 금액 불일치 등 복잡한 검증은 Service/PG 연동 계층에서 처리됩니다.
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CONFIRM_SUCCESS, response));
    }

    /**
     * POST /payments/cancel : 결제 전액 취소 (PAID -> CANCELED)
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(@Valid @RequestBody PaymentCancelRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.cancelPayment(request, userDetails);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_CANCEL_SUCCESS, response));
    }

    /**
     * GET /payments/{orderId} : 결제 내역 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable UUID orderId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_INQUIRY_SUCCESS, response));
    }

    /**
     * GET /payments?page=0&size=10 : 결제 내역 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<PaymentResponse>>> getAllPayments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Pageable 객체 생성 (기본 정렬: 생성일 기준 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponseDto<PaymentResponse> responsePageDto = paymentService.getAllPayments(pageable, userDetails);
        return ResponseEntity.ok(ApiResponse.success(PaymentSuccessCode.PAYMENT_ALL_INQUIRY_SUCCESS, responsePageDto));
    }
}