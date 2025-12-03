package com.destiny.orderservice.infrastructure.config;

import com.destiny.global.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler extends GlobalExceptionHandler {

}
