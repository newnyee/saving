package com.saving.user.controller;

import com.saving.category.service.CategoryService;
import com.saving.common.response.ApiResponse;
import com.saving.common.response.TokenResponse;
import com.saving.user.dto.LoginRequestDto;
import com.saving.user.dto.ReissueTokenRequestDto;
import com.saving.user.dto.UserCreateRequestDto;
import com.saving.user.dto.UserCreatedResponseDto;
import com.saving.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;

    @Operation(summary = "회원 가입", description = "사용자 정보에 의하여 계정이 생성됩니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserCreatedResponseDto> createUser(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {

        UserCreatedResponseDto user = userService.createUser(userCreateRequestDto);
        categoryService.createDefaultCategories(user.getId());

        return ApiResponse.created(user);
    }

    @Operation(summary = "로그인", description = "사용자의 계정과 비밀번호를 입력하여 인증 후 엑세스 토큰, 리프래쉬 토큰이 발급됩니다.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {

        return ApiResponse.ok(userService.authenticationAndCreateToken(loginRequestDto));
    }

    @Operation(summary = "토큰 재발급", description = "새로운 엑세스 토큰, 리프래쉬 토큰이 발급됩니다.")
    @PostMapping("/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TokenResponse> reissueToken(
            @RequestAttribute Long userId,
            @Valid @RequestBody ReissueTokenRequestDto reissueTokenRequestDto) {

        return ApiResponse.ok(userService.reissueToken(userId, reissueTokenRequestDto));
    }
}
