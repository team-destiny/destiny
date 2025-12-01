package com.destiny.userservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import com.destiny.userservice.application.service.AuthService;
import com.destiny.userservice.presentation.dto.request.UserLoginRequest;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.TokenResponse;
import com.destiny.userservice.presentation.dto.response.UserLoginResponse;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserSignUpResponse> signUp(
        @Valid @RequestBody UserSignUpRequest userSignUpRequest
    ) {
        UserSignUpResponse body = authService.signUp(userSignUpRequest);

        return ApiResponse.success(CommonSuccessCode.CREATED, body);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(
        @Valid @RequestBody UserLoginRequest userLoginRequest
    ) {
        String accessToken = "dummy-access-token";
        String refreshToken = "dummy-refresh-token";
        UserLoginResponse body = UserLoginResponse.of(accessToken, refreshToken);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken
    ) {
        // TODO: refreshToken 블랙리스트 처리 or 만료 처리
        // 지금은 더미라서 그냥 204
        return ApiResponse.success(CommonSuccessCode.NO_CONTENT);
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissueToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken
    ) {
        // TODO: refreshToken 검증 + 새 토큰 발급
        String newAccessToken = "dummy-new-access-token";
        String newRefreshToken = "dummy-new-refresh-token";

        TokenResponse body = TokenResponse.of(newAccessToken, newRefreshToken);
        return ApiResponse.success(CommonSuccessCode.OK, body);
    }



}
