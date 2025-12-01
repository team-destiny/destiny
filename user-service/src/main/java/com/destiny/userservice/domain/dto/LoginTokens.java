package com.destiny.userservice.domain.dto;

public record LoginTokens(
    String accessToken,
    String refreshToken
) {
    public static LoginTokens of(String accessToken, String refreshToken) {
        return new LoginTokens(accessToken, refreshToken);
    }
}
