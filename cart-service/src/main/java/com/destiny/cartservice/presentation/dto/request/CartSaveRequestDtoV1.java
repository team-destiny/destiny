package com.destiny.cartservice.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartSaveRequestDtoV1 {

    private Long productId;
    private Long optionId; // nullable
    private int quantity;

}
