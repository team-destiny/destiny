package com.destiny.couponservice.presentation.controller;

import com.destiny.couponservice.infrastructure.messaging.event.command.CouponValidateCommand;
import com.destiny.couponservice.infrastructure.messaging.producer.CouponValidateTestProducer;
import com.destiny.couponservice.presentation.dto.request.CouponValidateTestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/test/coupons")
public class CouponValidateTestController {

    private final CouponValidateTestProducer testProducer;

    @PostMapping("/validate")
    public ResponseEntity<Void> sendValidateRequest(
        @RequestBody CouponValidateTestRequest request
    ) {
        CouponValidateCommand command = new CouponValidateCommand(
            request.getCouponId(),
            request.getOriginalAmount()
        );

        testProducer.sendTest(command);
        return ResponseEntity.accepted().build();
    }
}
