package com.destiny.cartservice.presentation.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartFindItemResponse {

    private UUID cartId;
    private UUID productId;
    private UUID brandId;
    private int quantity;
    private String productName;
    private String optionName;
    private int price;

}
