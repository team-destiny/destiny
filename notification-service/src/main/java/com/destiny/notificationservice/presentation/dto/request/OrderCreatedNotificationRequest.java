package com.destiny.notificationservice.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreatedNotificationRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID brandId;

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String buyerName;

    @Email
    @NotBlank
    private String buyerEmail;

    @NotBlank
    private String productName;

    @NotBlank
    private String option;

    @Min(1)
    private int quantity;

    @Min(0)
    private int totalPrice;

    @NotBlank
    private String message;
}