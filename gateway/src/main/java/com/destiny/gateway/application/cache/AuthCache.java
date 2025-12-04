package com.destiny.gateway.application.cache;

public interface AuthCache {
    void storeToken(String userId, Long jwtValidator);

    Long getToken(String userId);

    void removeToken(String userId);
}
