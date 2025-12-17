package com.destiny.cartservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CartFindItemResponse implements Serializable {

    private UUID cartId;
    private UUID productId;
    private UUID brandId;
    private int quantity;
    private String productName;
    private String optionName;
    private int price;

    @JsonCreator
    public CartFindItemResponse(
        @JsonProperty("cartId") UUID cartId,
        @JsonProperty("productId") UUID productId,
        @JsonProperty("brandId") UUID brandId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("productName") String productName,
        @JsonProperty("optionName") String optionName,
        @JsonProperty("price") int price
    ) {
        this.cartId = cartId;
        this.productId = productId;
        this.brandId = brandId;
        this.quantity = quantity;
        this.productName = productName;
        this.optionName = optionName;
        this.price = price;
    }
}