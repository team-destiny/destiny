package com.destiny.userservice.infrastructure.redis.cache;

import com.destiny.userservice.application.cache.AuthCache;
import com.destiny.userservice.infrastructure.security.jwt.JwtProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRedisCache implements AuthCache {

    private static final String LOGOUT_KEY_PREFIX = "user:logoutAt:";
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;

    /**
     * 로그아웃 시점 기록
     * @param userId
     * @param logoutAtMillis
     */
    @Override
    public void storeToken(String userId, Long logoutAtMillis) {
        String key = buildCacheKey(userId);
        String value = String.valueOf(logoutAtMillis);
        Duration ttl = Duration.ofMinutes(jwtProperties.getAccessExpirationMillis());

        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Long getToken(String userId) {
        String key = buildCacheKey(userId);
        String stringValue = stringRedisTemplate.opsForValue().get(key);
        return stringValue != null ? Long.valueOf(stringValue) : null;
    }

    @Override
    public void removeToken(String userId) {
        String key = buildCacheKey(userId);
        stringRedisTemplate.delete(key);
    }

    private String buildCacheKey(String userUuid) {
        return LOGOUT_KEY_PREFIX + userUuid;
    }
}
