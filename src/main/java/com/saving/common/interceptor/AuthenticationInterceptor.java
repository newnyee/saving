package com.saving.common.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.saving.common.util.TokenUtils;
import com.saving.user.domain.repository.UserRepository;
import com.saving.user.exception.AuthTokenExpiredException;
import com.saving.user.exception.InvalidTokenException;
import com.saving.user.exception.NullTokenException;
import com.saving.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String PREFIX_FOR_TOKEN = "Bearer";
    private static final String ALLOW_EXPIRED_TOKEN_ENDPOINT = "/api/v1/users/tokens";

    private final TokenUtils tokenUtils;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            log.error("[NullTokenException] ex");
            throw new NullTokenException();
        }

        if (!authorization.startsWith(PREFIX_FOR_TOKEN)) {
            log.error("[InvalidTypeOfTokenException] ex");
            throw new InvalidTokenException();
        }

        String token = authorization.split(" ")[1];

        try {
            Long userId = tokenUtils.verifyToken(token);
            if (userRepository.existsById(userId)) {
                request.setAttribute("userId", userId);
                return true;
            }
            throw new UserNotFoundException();

        } catch (TokenExpiredException e) {
            /* 토큰 재발급 시 Access 토큰이 서버에서 발급된 토큰 이면서 만료된 토큰일 때에만
            토큰 재발급 API 호출 가능 */
            if (request.getRequestURI().equals(ALLOW_EXPIRED_TOKEN_ENDPOINT)) {
                request.setAttribute("userId",
                        tokenUtils.getUserIdFromToken(token));
                return true;
            }

            log.error("[{}] ex", e.getClass().getSimpleName(), e);
            throw new AuthTokenExpiredException();

        } catch (Exception e) {
            log.error("[{}] ex ", e.getClass().getSimpleName(), e);
            throw new InvalidTokenException();
        }
    }

}
