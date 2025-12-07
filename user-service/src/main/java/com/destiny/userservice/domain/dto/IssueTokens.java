package com.destiny.userservice.domain.dto;

public record IssueTokens(
    String accessToken,
    String refreshToken
) {
    public static IssueTokens of(String accessToken, String refreshToken) {
        return new IssueTokens(accessToken, refreshToken);
    }
}
