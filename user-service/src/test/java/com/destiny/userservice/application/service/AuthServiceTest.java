package com.destiny.userservice.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.exception.UserErrorCode;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.presentation.dto.request.UserSignUpRequest;
import com.destiny.userservice.presentation.dto.response.UserSignUpResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // 서비스에서 @Value("${user.admin.token}") 으로 주입받는 필드 강제로 세팅
        ReflectionTestUtils.setField(authService, "masterAdminToken", "test-admin-token");
    }

    private UserSignUpRequest createRequest(
        String username,
        String password,
        String email,
        UserRole userRole,
        String adminToken
    ) {
        // 네가 실제로 쓰는 record/DTO 생성자에 맞게 파라미터 맞춰줘
        return new UserSignUpRequest(
            username,
            password,
            email,
            userRole,
            "닉네임",
            "010-1111-2222",
            "01234",
            "서울시 어딘가 1길",
            "101동 1001호",
            LocalDate.of(2000, 1, 1),
            adminToken
        );
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void signUp_Fail_When_Username_Already_Exists() {
        // given
        UserSignUpRequest request = createRequest(
            "testUser",
            "Test1234!",
            "test@email.com",
            null,
            null
        );

        when(userRepository.existsByUsernameAndDeletedAtIsNull(request.username()))
            .thenReturn(true);

        //when
        BizException ex = assertThrows(BizException.class,
            () -> authService.signUp(request));

        //then
        assertEquals(UserErrorCode.USER_ALREADY_EXISTS, ex.getResponseCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 관리자 토큰")
    void signUp_Fail_When_AdminToken_Invalid() {
        // given
        UserSignUpRequest request = createRequest(
            "adminUser",
            "Admin1234!",
            "admin@email.com",
            UserRole.MASTER,
            "wrong-token"   // 요청 토큰
        );

        when(userRepository.existsByUsernameAndDeletedAtIsNull(request.username()))
            .thenReturn(false);

        // when
        BizException ex = assertThrows(BizException.class,
            () -> authService.signUp(request));

        // then
        assertEquals(UserErrorCode.INVALID_ADMIN_TOKEN, ex.getResponseCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 성공 - CUSTOMER 정상 회원가입")
    void signUp_Success_When_Valid_Customer() {
        // given
        UserSignUpRequest request = createRequest(
            "testUser",
            "Test1234!",
            "test@email.com",
            null,
            null
        );

        when(userRepository.existsByUsernameAndDeletedAtIsNull(request.username()))
            .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
            .thenReturn("encoded-password");

        // save() 호출 시 전달된 User 그대로 반환
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserSignUpResponse response = authService.signUp(request);

        // then
        assertNotNull(response);
        assertEquals("testUser", response.username());
        assertEquals(UserRole.CUSTOMER, response.userRole());

        verify(userRepository).existsByUsernameAndDeletedAtIsNull(request.username());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }
}