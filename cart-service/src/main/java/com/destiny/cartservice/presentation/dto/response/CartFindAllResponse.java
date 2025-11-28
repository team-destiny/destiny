package com.destiny.cartservice.presentation.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartFindAllResponse {

    private final List<CartFindItemResponse> items;

    public static CartFindAllResponse from(List<CartFindItemResponse> items) {
        return CartFindAllResponse.builder()
            .items(items).build();
    }

}
