package com.destiny.gateway.presentation.advice;

import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {
    private final GlobalError error;

    public GatewayException(GlobalError error) {
        super(error.getErrorMessage());
        this.error = error;
    }

    public GatewayException(GlobalError error, Throwable cause) {
        super(error.getErrorMessage(), cause);
        this.error = error;
    }
}
