package com.saving.user.domain.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    @Value("${token.expired-time.refresh}")
    private Long expiredRefreshTokenTime;
    @Value("${token.refresh-token-id-prefix}")
    private String refreshTokenIdPrefix;

    public void setRefreshToken(Long id, String refreshToken) {
        redisTemplate.opsForValue().set(refreshTokenIdPrefix + id, refreshToken,
                Duration.ofSeconds(expiredRefreshTokenTime));
    }

    public String getRefreshToken(Long id) {
        return redisTemplate.opsForValue().get(refreshTokenIdPrefix + id);
    }
}
