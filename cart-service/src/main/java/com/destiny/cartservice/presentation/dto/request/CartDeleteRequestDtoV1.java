package com.destiny.cartservice.presentation.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartDeleteRequestDtoV1 {

    private List<String> cartIds;

}
