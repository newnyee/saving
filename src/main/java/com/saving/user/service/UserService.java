package com.saving.user.service;

import com.saving.common.response.TokenResponse;
import com.saving.user.domain.repository.RedisRefreshTokenRepository;
import com.saving.common.util.TokenUtils;
import com.saving.user.domain.entity.User;
import com.saving.user.domain.repository.UserRepository;
import com.saving.user.dto.LoginRequestDto;
import com.saving.user.dto.ReissueTokenRequestDto;
import com.saving.user.dto.UserCreateRequestDto;
import com.saving.user.dto.UserCreatedResponseDto;
import com.saving.user.exception.DuplicateUserNameException;
import com.saving.user.exception.InvalidTokenException;
import com.saving.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Transactional
    public UserCreatedResponseDto createUser(UserCreateRequestDto userCreateRequestDto) {

        if (userRepository.existsByUsername(userCreateRequestDto.getUsername())) {
            throw new DuplicateUserNameException();
        }
        userCreateRequestDto.changePassword(passwordEncoder);

        return new UserCreatedResponseDto(userRepository.save(userCreateRequestDto.toEntity()));
    }

    @Transactional
    public TokenResponse authenticationAndCreateToken(LoginRequestDto loginRequestDto) {

        User getUser = userRepository
                .findByUsername(loginRequestDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        Long userId = getUser.getId();

        if (getUser.passwordMatches(passwordEncoder, loginRequestDto.getPassword())) {
            return createToken(userId);
        }

        throw new UserNotFoundException();
    }

    public TokenResponse reissueToken(Long userId, ReissueTokenRequestDto reissueTokenRequestDto) {

        String savedRefreshToken = redisRefreshTokenRepository.getRefreshToken(userId);

        if (savedRefreshToken.equals(reissueTokenRequestDto.getOldRefreshToken())) {
            return createToken(userId);
        }

        throw new InvalidTokenException();
    }

    public TokenResponse createToken(Long userId) {
        TokenResponse token = tokenUtils.createToken(userId);
        redisRefreshTokenRepository.setRefreshToken(userId, token.getRefreshToken());
        return token;
    }
}
