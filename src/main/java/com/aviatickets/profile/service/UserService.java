package com.aviatickets.profile.service;

import com.aviatickets.profile.config.AppProperties.JwtProperties;
import com.aviatickets.profile.controller.request.LoginRequest;
import com.aviatickets.profile.controller.response.RefreshTokenResponse;
import com.aviatickets.profile.controller.response.UserDto;
import com.aviatickets.profile.mapper.UserMapper;
import com.aviatickets.profile.model.User;
import com.aviatickets.profile.repository.UserRepository;
import com.aviatickets.profile.util.JwtUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final String UNAUTHORIZED_MESSAGE = "Invalid refresh token";
    public static final String FORBIDDEN_MESSAGE = "You are not authorized to perform this action";
    public static final String NOT_FOUND_USER = "User by id %d not found";

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public String login(String refreshToken) {
        Long userId;
        try {
            userId = JwtUtils.getIdFromToken(refreshToken, jwtProperties.refreshToken().secret());
        } catch (RuntimeException e) {
            throw new RuntimeException(UNAUTHORIZED_MESSAGE, e);
        }

        User user = repository.findById(userId).orElseThrow(() -> new AccessDeniedException(FORBIDDEN_MESSAGE));
        return JwtUtils.generateToken(user, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest loginRequest) {
        User user = repository.findByUsername(loginRequest.username()).orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AccessDeniedException(FORBIDDEN_MESSAGE);
        }

        return JwtUtils.generateToken(user, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());
    }

    @Transactional
    public RefreshTokenResponse signUp(LoginRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        user = saveUser(user);

        String refreshToken = JwtUtils.generateToken(user, jwtProperties.refreshToken().secret(), -1);
        String accessToken = JwtUtils.generateToken(user, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());

        return new RefreshTokenResponse(accessToken, refreshToken);
    }

    private User saveUser(User user) {
        return repository.save(user);
    }

    public Page<UserDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(userMapper::modelToDto);
    }

    public UserDto findById(@NotNull Long id) {
        User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException(NOT_FOUND_USER.formatted(id)));
        return userMapper.modelToDto(user);
    }

}
