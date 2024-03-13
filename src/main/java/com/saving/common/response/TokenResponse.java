package com.saving.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {

    @Schema(title = "access token", description = "엑세스 토큰")
    private String accessToken;

    @Schema(title = "token expired time", description = "엑세스 토큰 만료일")
    private String expiredTimeForAccessToken;

    @Schema(title = "refresh token", description = "리프래쉬 토큰")
    private String refreshToken;

    @Schema(title = "token expired time", description = "토큰 만료일")
    private String expiredTimeForRefreshToken;

    @Builder
    public TokenResponse(
            String accessToken, String expiredTimeForAccessToken,
            String refreshToken, String expiredTimeForRefreshToken) {

        this.accessToken = accessToken;
        this.expiredTimeForAccessToken = expiredTimeForAccessToken;
        this.refreshToken = refreshToken;
        this.expiredTimeForRefreshToken = expiredTimeForRefreshToken;
    }
}
