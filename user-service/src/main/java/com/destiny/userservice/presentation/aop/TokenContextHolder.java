package com.destiny.userservice.presentation.aop;

import com.destiny.userservice.domain.dto.IssueTokens;

/**
 * ThreadLocal을 사용하여 LoginTokens를 임시 저장
 */
public class TokenContextHolder {

    private static final ThreadLocal<IssueTokens> tokenHolder  = new ThreadLocal<>();

    public static void setToken(IssueTokens tokens) {
        tokenHolder.set(tokens);
    }

    public static IssueTokens getTokens() {
        return tokenHolder.get();
    }

    public static IssueTokens getAndClear() {
        IssueTokens tokens = tokenHolder.get();
        tokenHolder.remove();
        return tokens;
    }

    public static void clear() {
        tokenHolder.remove();
    }
}
