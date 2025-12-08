package com.example.reviewservice.infrastructure.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.reviewservice")
public class FeignClientConfig {

}
