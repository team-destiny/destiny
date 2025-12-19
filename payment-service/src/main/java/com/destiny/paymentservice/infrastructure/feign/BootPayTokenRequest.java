package com.destiny.paymentservice.infrastructure.feign;

public record BootPayTokenRequest(
    String application_id,
    String private_key
) {}