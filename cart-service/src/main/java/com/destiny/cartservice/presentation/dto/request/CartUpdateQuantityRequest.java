package com.destiny.cartservice.presentation.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartUpdateQuantityRequest {

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다")
    private int quantity;

}
