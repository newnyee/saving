package com.saving.common.interceptor;

import com.saving.common.util.TokenUtil;
import com.saving.user.domain.repository.UserRepository;
import com.saving.user.exception.InvalidTokenException;
import com.saving.user.exception.TokenExpiredException;
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
    private static final String AUTHORIZATION = "Authorization";

    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String authorization = request.getHeader(AUTHORIZATION);

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
            Long userId = tokenUtil.verifyToken(token);
            if (userRepository.existsById(userId)) {
                request.setAttribute("userId", userId);
                return true;
            }
            throw new UserNotFoundException();

        } catch (TokenExpiredException e) {
            log.error("[{}] ex", e.getClass().getSimpleName(), e);
            throw new TokenExpiredException();

        } catch (Exception e) {
            log.error("[{}] ex ", e.getClass().getSimpleName(), e);
            throw new InvalidTokenException();
        }
    }
}
