package com.saving.common.config.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.saving.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${token.expired-time.access}")
    private Long expiredAccessTokenMs;

    @Value("${token.expired-time.refresh}")
    private Long expiredRefreshTokenMs;

    @Bean
    public TokenUtil jwtUtil() {
        return new TokenUtil(Algorithm.HMAC256(jwtSecretKey), expiredAccessTokenMs, expiredRefreshTokenMs);
    }
}
