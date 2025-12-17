package com.destiny.cartservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CartFindAllResponse implements Serializable {

    private List<CartFindItemResponse> items;

    @JsonCreator
    public CartFindAllResponse(@JsonProperty("items") List<CartFindItemResponse> items) {
        this.items = items;
    }

    public static CartFindAllResponse from(List<CartFindItemResponse> items) {
        return CartFindAllResponse.builder()
            .items(items).build();
    }
}