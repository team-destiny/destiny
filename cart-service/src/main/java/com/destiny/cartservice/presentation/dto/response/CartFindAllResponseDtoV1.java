package com.destiny.cartservice.presentation.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartFindAllResponseDtoV1 {

    private final List<CartFindItemResponseDtoV1> items;

    public static CartFindAllResponseDtoV1 from(List<CartFindItemResponseDtoV1> items) {
        return CartFindAllResponseDtoV1.builder()
            .items(items).build();
    }

}
