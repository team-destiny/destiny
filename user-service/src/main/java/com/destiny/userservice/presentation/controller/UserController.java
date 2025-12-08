package com.destiny.userservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import com.destiny.userservice.application.service.UserService;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.entity.UserStatus;
import com.destiny.userservice.infrastructure.security.auth.CustomUserDetails;
import com.destiny.userservice.presentation.advice.PageingUtils;
import com.destiny.userservice.presentation.advice.UserSuccessCode;
import com.destiny.userservice.presentation.dto.request.UserPasswordUpdateRequest;
import com.destiny.userservice.presentation.dto.request.UserUpdateRequest;
import com.destiny.userservice.presentation.dto.response.UserGetResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserGetResponse> getUser(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable UUID userId) {

        UUID authUserId = customUserDetails.getUserId();
        UserRole authUserRole = UserRole.valueOf(customUserDetails.getUserRole());

        UserGetResponse body = userService.getUser(authUserId, authUserRole, userId);

        return ApiResponse.success(UserSuccessCode.USER_GET, body);
    }

    @PatchMapping("/{userId}")
    public ApiResponse<UserGetResponse> updateUser(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable UUID userId,
        @Valid @RequestBody UserUpdateRequest userUpdateRequest) {

        UUID authUserId = customUserDetails.getUserId();
        UserRole authUserRole = UserRole.valueOf(customUserDetails.getUserRole());

        UserGetResponse body = userService.updateUser(authUserId, authUserRole, userId, userUpdateRequest);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @PostMapping("/{userId}/password")
    public ApiResponse updatePassword(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable UUID userId,
        @Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        UUID authUserId = customUserDetails.getUserId();
        UserRole authUserRole = UserRole.valueOf(customUserDetails.getUserRole());

        userService.updatePassword(authUserId, authUserRole, userId, userPasswordUpdateRequest);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable UUID userId) {
        UUID authUserId = customUserDetails.getUserId();
        UserRole authUserRole = UserRole.valueOf(customUserDetails.getUserRole());

        userService.deleteUser(authUserId, authUserRole, userId);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping
    public ApiResponse<List<UserGetResponse>> getUsers(
        @RequestParam(defaultValue = "false")  boolean deleted,
        @RequestParam(required = false) UserRole userRole,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sortBy,          // "createdAt" / "updatedAt"
        @RequestParam(defaultValue = "true") boolean isDescending
    ) {

        Pageable pageable = PageingUtils.createPageable(size, sortBy, isDescending);

        List<UserGetResponse> responses =
            userService.getUsers(deleted, userRole, searchType, keyword, pageable);

        return ApiResponse.success(CommonSuccessCode.OK, responses);
    }

    @PostMapping("/{userId}/status")
    public ApiResponse updateUserStatus(@PathVariable UUID userId, @NotNull(message="변경할 상태는 필수 값입니다.") @RequestParam UserStatus userStatus) {
        // TODO : userService.updateUserStatus(userId);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

}
