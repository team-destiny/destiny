package com.destiny.userservice.application.service;

import com.destiny.global.code.CommonErrorCode;
import com.destiny.global.exception.BizException;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserInfo;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.repository.UserRepository;
import com.destiny.userservice.presentation.advice.UserErrorCode;
import com.destiny.userservice.presentation.dto.request.UserPasswordUpdateRequest;
import com.destiny.userservice.presentation.dto.request.UserUpdateRequest;
import com.destiny.userservice.presentation.dto.response.UserGetResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserGetResponse getUser(UUID authUserId, UserRole authUserRole, UUID targetUserId) {
        validateAccess(authUserId, authUserRole, targetUserId);
        User user = userRepository.findById(targetUserId);

        return UserGetResponse.of(user);
    }

    @Transactional
    public UserGetResponse updateUser(UUID authUserId, UserRole authUserRole, UUID targetUserId, UserUpdateRequest userUpdateRequest) {
        validateAccess(authUserId, authUserRole, targetUserId);
        User user = userRepository.findByUserIdAndDeletedAtIsNull(targetUserId);

        if(userUpdateRequest.email() != null) {
            user.changeEmail(userUpdateRequest.email());
        }

        UserInfo userInfo = user.getUserInfo();
        if(userInfo != null) {
            userInfo.updateProfile(userUpdateRequest);
        }

        return UserGetResponse.of(user);
    }

    /**
     *  다른 사람이 요청하는 경우 : 관리자만 허용
     */
    private void validateAccess(UUID authUserId, UserRole authUserRole, UUID targetUserId) {
        if (!targetUserId.equals(authUserId) && authUserRole != UserRole.MASTER) {
            throw new BizException(CommonErrorCode.ACCESS_DENIED);
        }
    }

    public List<UserGetResponse> getUsers(boolean deleted, UserRole userRole, String searchType, String keyword, Pageable pageable) {
        // keyword 없으면 searchType 무시
        String safeSearchType = StringUtils.hasText(keyword) ? searchType : null;

        Page<User> userPage = userRepository.searchUsers(
            deleted,
            userRole,
            safeSearchType,
            keyword,
            pageable
        );

        return userPage
            .stream()
            .map(UserGetResponse::of)
            .toList();

    }

    @Transactional
    public void updatePassword(UUID authUserId, UserRole authUserRole, UUID userId, UserPasswordUpdateRequest userPasswordUpdateRequest) {
        validateAccess(authUserId, authUserRole, userId);
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId);

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(userPasswordUpdateRequest.currentPassword(), user.getPassword())) {
            throw new BizException(UserErrorCode.PASSWORD_NOT_MATCH);
        }

        String encodedNewPassword = passwordEncoder.encode(userPasswordUpdateRequest.newPassword());
        user.changePassword(encodedNewPassword);
    }

    @Transactional
    public void deleteUser(UUID authUserId, UserRole authUserRole, UUID userId) {
        validateAccess(authUserId, authUserRole, userId);
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId);
        user.markDeleted(userId);
        UserInfo userInfo = user.getUserInfo();
        if (userInfo != null) {
            userInfo.markDeleted(userId);
        }
    }

}
