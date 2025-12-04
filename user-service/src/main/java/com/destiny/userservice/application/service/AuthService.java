package com.destiny.userservice.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.userservice.application.cache.AuthCache;
import com.destiny.userservice.domain.dto.LoginTokens;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.userservice.infrastructure.security.jwt.JwtTokenGenerator;
import com.destiny.userservice.presentation.advice.UserErrorCode;
import com.destiny.userservice.presentation.aop.TokenContextHolder;
import com.destiny.userservice.presentation.dto.request.UserLoginRequest;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.UserLoginResponse;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
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
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthCache authCache;

    @Value("${user.admin.token}")
    private String masterAdminToken;   // 설정에서 주입

    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest) {
        if(userRepository.existsByUsernameAndDeletedAtIsNull(userSignUpRequest.username())){
            throw new BizException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        UserRole userRole = userSignUpRequest.userRole();
        if(userRole == UserRole.MASTER){
            validationAdminToken(userSignUpRequest.adminToken());
        }

        User user = User.createUser(
            userSignUpRequest.username()
            , passwordEncoder.encode(userSignUpRequest.password())
            , userSignUpRequest.email()
            , userRole
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

        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshToken =jwtTokenGenerator.generateRefreshToken(user.getUserId().toString());

        TokenContextHolder.setToken(new LoginTokens(accessToken, refreshToken));

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

}
