package com.destiny.cartservice.presentation.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartFindItemResponse {

    private final UUID cartId;
    private final UUID productId;
    private final UUID brandId;
    private final int quantity;
    private final String productName;
    private final String optionName;
    private final int price;

}
