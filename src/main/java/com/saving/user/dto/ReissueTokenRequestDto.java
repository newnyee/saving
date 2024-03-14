package com.saving.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReissueTokenRequestDto {

    @Schema(title = "refresh token", description = "사용자가 보유중인 리프래쉬 토큰")
    @NotBlank(message = "필수 입력값 입니다.")
    private String oldRefreshToken;
}
