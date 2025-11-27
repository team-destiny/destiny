package com.destiny.cartservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartSaveResponseDtoV1 {

    private final String cartId;
    private final Long productId;
    private final Long optionId;
    private final int quantity;
    private final String productName;
    private final String optionName;
    private final int price;
}
