package com.destiny.userservice.presentation.aop;

import com.destiny.userservice.domain.dto.LoginTokens;

/**
 * ThreadLocal을 사용하여 LoginTokens를 임시 저장
 */
public class TokenContextHolder {

    private static final ThreadLocal<LoginTokens> tokenHolder  = new ThreadLocal<>();

    public static void setToken(LoginTokens tokens) {
        tokenHolder.set(tokens);
    }

    public static LoginTokens getTokens() {
        return tokenHolder.get();
    }

    public static LoginTokens getAndClear() {
        LoginTokens tokens = tokenHolder.get();
        tokenHolder.remove();
        return tokens;
    }

    public static void clear() {
        tokenHolder.remove();
    }
}
