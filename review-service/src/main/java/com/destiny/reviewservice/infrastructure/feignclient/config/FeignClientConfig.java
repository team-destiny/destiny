package com.destiny.reviewservice.infrastructure.feignclient.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.destiny.reviewservice")
public class FeignClientConfig {

}
