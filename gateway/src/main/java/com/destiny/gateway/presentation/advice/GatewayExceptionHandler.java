package com.destiny.gateway.presentation.advice;

import com.destiny.gateway.presentation.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleGatewayException(GatewayException exception) {
        return Mono.just(
            ResponseEntity
                .status(exception.getError().getHttpStatus())
                .body(ApiResponse.builder()
                    .message(exception.getError().getErrorMessage())
                    .build())
        );
    }
}
