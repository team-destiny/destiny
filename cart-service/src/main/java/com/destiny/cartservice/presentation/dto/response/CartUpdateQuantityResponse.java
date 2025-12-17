package com.destiny.cartservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartUpdateQuantityResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID cartId;
    private final UUID productId;
    private final UUID brandId;
    private final int quantity;
    private final String productName;
    private final String optionName;
    private final int price;

    @JsonCreator
    public CartUpdateQuantityResponse(
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