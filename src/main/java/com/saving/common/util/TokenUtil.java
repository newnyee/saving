package com.saving.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saving.common.response.TokenResponse;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenUtil {

    private static final String CLAIM_USER_ID = "userId";

    private final Algorithm algorithm;
    private final Long expiredAccessTokenMs;
    private final Long expiredRefreshTokenMs;

    public TokenResponse createToken(Long userId) {

        Date expiredTimeForAccessToken =
                new Date(System.currentTimeMillis() + expiredAccessTokenMs);
        Date expiredTimeForRefreshToken =
                new Date(System.currentTimeMillis() + expiredRefreshTokenMs * 1000);

        return TokenResponse.builder()
                .accessToken(JWT.create()
                        .withClaim(CLAIM_USER_ID, userId)
                        .withExpiresAt(expiredTimeForAccessToken)
                        .sign(algorithm))
                .expiredTimeForAccessToken(expiredTimeForAccessToken.toString())
                .refreshToken(UUID.randomUUID().toString())
                .expiredTimeForRefreshToken(expiredTimeForRefreshToken.toString())
                .build();
    }

    public Long verifyToken(String token) {

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT verified = verifier.verify(token);

        return verified.getClaim(CLAIM_USER_ID).asLong();
    }
}
