package com.destiny.userservice.infrastructure.redis.cache;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenBlacklistCache {

    private static final String LOGOUT_KEY_PREFIX = "user:logoutAt:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 로그아웃 시점 기록
     * @param userId
     * @param logoutAtMillis
     */
    public void saveLogoutTime(String userId, long logoutAtMillis) {
        String key = buildLogoutKey(userId);
        String value = String.valueOf(logoutAtMillis);

        // TTL 30분
        Duration ttl = Duration.ofMinutes(30);

        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 
     * @param userId
     * @param tokenIssuedAtMillis
     * @return true 이면 무효화 토큰
     */
    public boolean isTokenInvalidated(String userId, long tokenIssuedAtMillis) {
        String key = buildLogoutKey(userId);
        String logoutAtStr = stringRedisTemplate.opsForValue().get(key);

        if (logoutAtStr == null) {
            return false;
        }

        long logoutAtMillis = Long.parseLong(logoutAtStr);
        // 토큰 발급 시각이 로그아웃 시간보다 이전이면 무효
        return tokenIssuedAtMillis < logoutAtMillis;
    }

    private String buildLogoutKey(String userUuid) {
        return LOGOUT_KEY_PREFIX + userUuid;
    }

}
