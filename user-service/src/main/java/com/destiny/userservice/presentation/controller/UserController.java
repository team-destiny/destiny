package com.destiny.userservice.presentation.controller;

import com.destiny.global.code.CommonSuccessCode;
import com.destiny.global.response.ApiResponse;
import com.destiny.userservice.application.service.UserService;
import com.destiny.userservice.domain.entity.User;
import com.destiny.userservice.domain.entity.UserRole;
import com.destiny.userservice.domain.entity.UserStatus;
import com.destiny.userservice.presentation.dto.request.UserPasswordUpdateRequest;
import com.destiny.userservice.presentation.dto.request.UserUpdateRequest;
import com.destiny.userservice.presentation.dto.response.UserGetResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
    // TODO : userSuccessCode 만들기

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserGetResponse> getUser(@PathVariable UUID userId) {
        // TODO : userService.getUser(userId);

        User user = getMockUser(userId);
        UserGetResponse body = UserGetResponse.of(user);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @PatchMapping("/{userId}")
    public ApiResponse<UserGetResponse> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        // TODO : userService.uadateUser(userId, uesrGetRequest);

        User user = getMockUser(userId);
        UserGetResponse body = UserGetResponse.of(user);

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    // TODO : userService 생성 후 제거
    private User getMockUser(UUID userId) {
        User user = User.createUser(
            "username"
            , "a123!@#"
            , "master@email.com"
            , UserRole.MASTER
            , "관리자"
            ,"010-1234-5678"
            , "12345"
            , "서울 송파구"
            , "101호"
            , LocalDate.now()
        );

        return user;
    }

    @PostMapping("/{userId}/password")
    public ApiResponse updatePassword(@PathVariable UUID userId, @Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        // TODO : userService.updatePassword(userId, requestDto);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(@PathVariable UUID userId) {
        // TODO : userService.deleteUser(userId);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

    @GetMapping
    public ApiResponse<List<UserGetResponse>> getUsers() {
        // TODO : userService.getUsers();

        List<User> users = List.of(
            getMockUser(UUID.randomUUID()),
            getMockUser(UUID.randomUUID())
        );

        List<UserGetResponse> body = users.stream()
            .map(UserGetResponse::of)
            .toList();

        return ApiResponse.success(CommonSuccessCode.OK, body);
    }

    @PostMapping("/{userId}/status")
    public ApiResponse updateUserStatus(@PathVariable UUID userId, @NotNull(message="변경할 상태는 필수 값입니다.") @RequestParam UserStatus userStatus) {
        // TODO : userService.updateUserStatus(userId);

        return ApiResponse.success(CommonSuccessCode.OK);
    }

}
