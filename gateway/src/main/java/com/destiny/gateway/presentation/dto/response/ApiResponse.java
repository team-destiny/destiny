package com.destiny.gateway.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    @Builder.Default
    private final String message = "success";
    private T data;

}
