package com.destiny.notificationservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.cache.retry")
public record NotificationCacheRetryProperties(
    int maxAttempts,
    long delayMillis
) {
    public NotificationCacheRetryProperties {
        if (maxAttempts <= 0) maxAttempts = 3;
        if (delayMillis <= 0) delayMillis = 300;
    }
}
