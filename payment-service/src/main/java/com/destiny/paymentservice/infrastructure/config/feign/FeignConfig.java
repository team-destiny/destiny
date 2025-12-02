package com.destiny.paymentservice.infrastructure.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.destiny.paymentservice")
public class FeignConfig {

}