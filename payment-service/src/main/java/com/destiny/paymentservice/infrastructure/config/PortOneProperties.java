package com.destiny.paymentservice.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.portone")
public class PortOneProperties {
    private String storeId;
    private String channelKey;
    private String channelGroupId;
    private String apiSecret;
}