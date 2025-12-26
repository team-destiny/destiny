package com.destiny.paymentservice.infrastructure.feign;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BootPayConfirmPayload {
    private String receipt_id;

    private String application_id;
}