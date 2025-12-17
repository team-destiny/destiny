package com.destiny.cartservice.presentation.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartFindAllResponse {

    private List<CartFindItemResponse> items;

    public static CartFindAllResponse from(List<CartFindItemResponse> items) {
        return CartFindAllResponse.builder()
            .items(items).build();
    }

}
