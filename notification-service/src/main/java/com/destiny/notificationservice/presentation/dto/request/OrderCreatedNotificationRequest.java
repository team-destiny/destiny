package com.destiny.notificationservice.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record OrderCreatedNotificationRequest(

    @NotNull UUID orderId,
    @NotNull UUID brandId,
    @NotBlank String orderNumber,
    @NotBlank String buyerName,
    @Email @NotBlank String buyerEmail,
    @NotBlank String productName,
    @NotBlank String option,
    @Min(1) int quantity,
    @Min(0) int totalPrice,
    @NotBlank String message
) {

}