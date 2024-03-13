package com.saving.user.service;

import com.saving.common.response.TokenResponse;
import com.saving.user.domain.repository.RedisRefreshTokenRepository;
import com.saving.common.util.TokenUtil;
import com.saving.user.domain.entity.User;
import com.saving.user.domain.repository.UserRepository;
import com.saving.user.dto.LoginRequestDto;
import com.saving.user.dto.UserCreateRequestDto;
import com.saving.user.dto.UserCreatedResponseDto;
import com.saving.user.exception.DuplicateUserNameException;
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
    private final TokenUtil tokenUtil;
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
            TokenResponse createdToken = tokenUtil.createToken(userId);
            redisRefreshTokenRepository.setRefreshToken(userId, createdToken.getRefreshToken());
            return createdToken;
        }

        throw new UserNotFoundException();
    }
}
