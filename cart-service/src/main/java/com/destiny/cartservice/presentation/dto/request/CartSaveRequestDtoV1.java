package com.destiny.cartservice.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartSaveRequestDtoV1 {

    private UUID productId;
    private UUID optionId; // nullable
    private int quantity;

}
