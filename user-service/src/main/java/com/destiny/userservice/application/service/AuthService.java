package com.destiny.userservice.application.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.userservice.application.cache.AuthCache;
import com.destiny.userservice.domain.dto.IssueTokens;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.userservice.infrastructure.security.jwt.JwtUtil;
import com.destiny.userservice.presentation.advice.UserErrorCode;
import com.destiny.userservice.presentation.aop.TokenContextHolder;
import com.destiny.userservice.presentation.dto.request.MasterSignUpRequest;
import com.destiny.userservice.presentation.dto.request.UserLoginRequest;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.UserLoginResponse;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthCache authCache;

    @Value("${user.admin.token}")
    private String masterAdminToken;   // 설정에서 주입

    @Transactional
    public UserSignUpResponse userSignUp(UserSignUpRequest userSignUpRequest) {
        if(userRepository.existsByUsernameAndDeletedAtIsNull(userSignUpRequest.username())){
            throw new BizException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.createUser(
            userSignUpRequest.username()
            , passwordEncoder.encode(userSignUpRequest.password())
            , userSignUpRequest.email()
            , userSignUpRequest.userRole()
            , userSignUpRequest.nickname()
            , userSignUpRequest.phone()
            , userSignUpRequest.zipcode()
            , userSignUpRequest.address1()
            , userSignUpRequest.address2()
            , userSignUpRequest.birth()
        );

        User savedUser = userRepository.save(user);

        log.info("회원가입 성공 - userId={}, username={}, role={}",
            savedUser.getUserId(), savedUser.getUsername(), savedUser.getUserRole());

        return UserSignUpResponse.of(savedUser);
    }

    @Transactional
    public UserSignUpResponse masterSignUp(MasterSignUpRequest masterSignUpRequest) {
        if(userRepository.existsByUsernameAndDeletedAtIsNull(masterSignUpRequest.username())){
            throw new BizException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        UserRole userRole = masterSignUpRequest.userRole();
        if(userRole == UserRole.MASTER){
            validationAdminToken(masterSignUpRequest.adminToken());
        }

        User user = User.createMaster(
            masterSignUpRequest.username()
            , passwordEncoder.encode(masterSignUpRequest.password())
            , masterSignUpRequest.email()
        );

        User savedUser = userRepository.save(user);

        log.info("회원가입 성공 - userId={}, username={}, role={}",
            savedUser.getUserId(), savedUser.getUsername(), savedUser.getUserRole());

        return UserSignUpResponse.of(savedUser);
    }

    private void validationAdminToken(String requestToken) {
        if(requestToken == null || !requestToken.equals(masterAdminToken)){
            log.warn("관리자 회원가입 시도 실패 - 잘못된 adminToken 사용");
            throw new BizException(UserErrorCode.INVALID_ADMIN_TOKEN);
        }
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.username());

        if(!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())){
            throw new BizException(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        TokenContextHolder.setToken(new IssueTokens(accessToken, refreshToken));

        log.info("로그인 성공 - userId={}, username={}", user.getUserId(), user.getUsername());

        return UserLoginResponse.of(user);
    }

    public void logout(CustomUserDetails customUserDetails, UUID targetUserId) {
        UUID authUserId = customUserDetails.getUserId();
        UserRole authUserRole = UserRole.valueOf(customUserDetails.getUserRole());
        long logoutMillis = Instant.now().toEpochMilli();

        verifyAuthUserOrThrow(authUserId);
        UUID logoutUserId = (targetUserId != null) ? targetUserId : authUserId;
        validateAccess(authUserId, authUserRole, logoutUserId);

         User logoutUser = userRepository.findById(logoutUserId);

        // 실제 로그아웃 처리
        authCache.storeToken(logoutUserId.toString(), logoutMillis);
    }

    /**
     *  다른 사람이 로그아웃 시키는 경우 : 관리자만 허용
     */
    private void validateAccess(UUID authUserId, UserRole authUserRole, UUID logoutUserId) {
        if (!logoutUserId.equals(authUserId) && authUserRole != UserRole.MASTER) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }


    private void verifyAuthUserOrThrow(UUID userId) {
        userRepository.existsByUserIdAndDeletedAtIsNull(userId);
    }

    public void reissueAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BizException(UserErrorCode.REFRESH_TOKEN_MISSING);
        }

        DecodedJWT decodedJwt = jwtUtil.verifyRefreshToken(refreshToken);
        UUID userId = UUID.fromString(decodedJwt.getClaim("userId").asString());
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId);

        String accessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = null;
        if(needRotate(decodedJwt)){
            newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());
        }

        IssueTokens tokens = newRefreshToken == null
            ? new IssueTokens(accessToken, null)
            : new IssueTokens(accessToken, newRefreshToken);
        TokenContextHolder.setToken(tokens);
    }

    private boolean needRotate(DecodedJWT refreshToken) {
        Instant now = Instant.now();
        Duration remain = Duration.between(now, refreshToken.getExpiresAtAsInstant());
        long remainDays = remain.toDays();

        return remainDays <= 3;   // 남은 기간 3일 이하면 true
    }
}
