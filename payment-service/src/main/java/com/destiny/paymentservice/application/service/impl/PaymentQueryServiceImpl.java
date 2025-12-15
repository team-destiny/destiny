package com.destiny.paymentservice.application.service.impl;

import com.destiny.global.exception.BizException;
import com.destiny.global.response.PageResponseDto;
import com.destiny.paymentservice.application.exception.PaymentErrorCode;
import com.destiny.paymentservice.application.service.inter.PaymentQueryService;
import com.destiny.paymentservice.domain.entity.Payment;
import com.destiny.paymentservice.domain.repository.PaymentRepository;
import com.destiny.paymentservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.paymentservice.presentation.dto.response.PaymentResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    // 결제 단건 조회
    public PaymentResponse getPaymentByOrderId(UUID orderId, CustomUserDetails userDetails) {
        Payment payment = switch (userDetails.getUserRole()) {
            case "CUSTOMER" -> paymentRepository.findByOrderIdAndUserId(orderId, userDetails.getUserId()).orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            case "MASTER" -> paymentRepository.findByOrderId(orderId).orElseThrow(() -> new BizException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            default -> throw new BizException(PaymentErrorCode.FORBIDDEN_ACCESS);
        };

        return PaymentResponse.fromEntity(payment);
    }

    // 결제 내역 전체 조회 (페이징)
    @Transactional(readOnly = true)
    public PageResponseDto<PaymentResponse> getAllPayments(Pageable pageable, CustomUserDetails userDetails) {

        Page<Payment> paymentPage = switch (userDetails.getUserRole()) {
            case "CUSTOMER" -> paymentRepository.findAllByUserId(userDetails.getUserId(), pageable);
            case "MASTER" -> paymentRepository.findAll(pageable);
            default -> throw new BizException(PaymentErrorCode.FORBIDDEN_ACCESS);
        };

        Page<PaymentResponse> responsePage = paymentPage.map(PaymentResponse::fromEntity);

        return PageResponseDto.from(responsePage);
    }
}