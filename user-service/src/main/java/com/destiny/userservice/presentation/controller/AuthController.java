package com.destiny.userservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import com.destiny.userservice.application.service.AuthService;
import com.destiny.userservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.userservice.presentation.advice.UserSuccessCode;
import com.destiny.userservice.presentation.annotation.IssueTokens;
import com.destiny.userservice.presentation.dto.request.UserLoginRequest;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.UserLoginResponse;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope // /actuator/refresh 엔드포인트를 호출하여 설정 변경 사항을 동적으로 반영
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auths")
public class AuthController {

    private final AuthService authService;

    @Value("${server.port}")
    private String serverPort;

    @Value("${message}")
    private String message;

    @GetMapping("/config-test")
    public String getConfig() {
        return "Product detail from PORT : " + serverPort + " and message : " + this.message ;
    }

    @PostMapping("/signup")
    public ApiResponse<UserSignUpResponse> signUp(
        @Valid @RequestBody UserSignUpRequest userSignUpRequest
    ) {
        UserSignUpResponse body = authService.signUp(userSignUpRequest);

        return ApiResponse.success(CommonSuccessCode.CREATED, body);
    }

    @IssueTokens
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(
        @Valid @RequestBody UserLoginRequest userLoginRequest
    ) {
        UserLoginResponse body = authService.login(userLoginRequest);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @IssueTokens
    @PostMapping("/reissue")
    public ApiResponse<Void> reissueToken(
        @CookieValue(name = "X-Refresh-Token", required = false) String refreshToken
    ) {
        authService.reissueAccessToken(refreshToken);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(value = "userId", required = false) UUID targetUserId
    ) {
        authService.logout(customUserDetails, targetUserId);

        return ApiResponse.success(UserSuccessCode.LOGOUT);
    }



}
