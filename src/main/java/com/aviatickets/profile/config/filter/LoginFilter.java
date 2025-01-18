package com.aviatickets.profile.config.filter;

import com.aviatickets.profile.config.AppProperties.JwtProperties;
import com.aviatickets.profile.dto.CustomPrincipal;
import com.aviatickets.profile.repository.UserRepository;
import com.aviatickets.profile.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;

import static com.aviatickets.profile.controller.ControllerConstants.ACCESS_TOKEN_COOKIE;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends OncePerRequestFilter {

    private final UserRepository repository;
    private final JwtProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        tryToAuthenticate(request);
        filterChain.doFilter(request, response);
    }

    private void tryToAuthenticate(HttpServletRequest request) {
        String token = extractToken(request);

        if (ObjectUtils.isEmpty(token)) return;

        Long id;

        try {
            id = JwtUtils.getIdFromToken(token, properties.accessToken().secret());

            if(JwtUtils.isExpired(token, properties.accessToken().secret())) {
                log.debug("Token for user with id {} is expired", id);
                return;
            }

        } catch (RuntimeException e) {
            log.error("Failed to extract id from token", e);
            return;
        }

        repository.findById(id).ifPresent(user -> {
            var authentication = new UsernamePasswordAuthenticationToken(
                    new CustomPrincipal(user.getId(), ZonedDateTime.now()), null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_TOKEN_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
