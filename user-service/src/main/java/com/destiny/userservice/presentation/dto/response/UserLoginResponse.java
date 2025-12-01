package com.destiny.userservice.presentation.dto.response;

public record UserLoginResponse(
    String accessToken,
    String refreshToken
) {
    public static UserLoginResponse of(String accessToken, String refreshToken) {
        return new UserLoginResponse(accessToken, refreshToken);
    }
}
