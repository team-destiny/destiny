package com.destiny.cartservice.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartSaveRequest {

    @NotNull(message = "상품 ID는 필수입니다")
    private UUID productId;

    private UUID optionId; // nullable

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다")
    private int quantity;

}
