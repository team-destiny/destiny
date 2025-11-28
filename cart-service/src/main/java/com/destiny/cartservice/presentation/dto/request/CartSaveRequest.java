package com.destiny.cartservice.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartSaveRequest {

    private UUID productId;
    private UUID optionId; // nullable
    private int quantity;

}
