package com.destiny.userservice.application.service;

import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.exception.UserErrorCode;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
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
        if(requestToken == null || !MessageDigest.isEqual(requestToken.getBytes(), masterAdminToken.getBytes())){

            log.warn("관리자 회원가입 시도 실패 - 잘못된 adminToken 사용");
            throw new BizException(UserErrorCode.INVALID_ADMIN_TOKEN);
        }
    }
}