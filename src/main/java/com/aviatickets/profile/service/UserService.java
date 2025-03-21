package com.aviatickets.profile.service;

import com.aviatickets.profile.config.AppProperties.JwtProperties;
import com.aviatickets.profile.controller.request.LoginRequest;
import com.aviatickets.profile.controller.request.UpdateUserRequest;
import com.aviatickets.profile.controller.response.TokenResponse;
import com.aviatickets.profile.controller.response.UserDto;
import com.aviatickets.profile.exception.UnauthorizedException;
import com.aviatickets.profile.exception.UsernameAlreadyExistsException;
import com.aviatickets.profile.mapper.UserMapper;
import com.aviatickets.profile.model.User;
import com.aviatickets.profile.model.UserEvent;
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
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class UserService {

    public static final String UNAUTHORIZED_MESSAGE = "Invalid refresh token";
    public static final String FORBIDDEN_MESSAGE = "You are not authorized to perform this action";
    public static final String NOT_FOUND_USER = "User by id %d not found";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    @Transactional(readOnly = true)
    public String login(String refreshToken) {
        Long userId;
        try {
            userId = JwtUtils.getIdFromToken(refreshToken, jwtProperties.refreshToken().secret());
        } catch (RuntimeException e) {
            throw new RuntimeException(UNAUTHORIZED_MESSAGE, e);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new AccessDeniedException(FORBIDDEN_MESSAGE));
        return JwtUtils.generateToken(user, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username()).orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UnauthorizedException(FORBIDDEN_MESSAGE);
        }

        String accessToken = JwtUtils.generateToken(user, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());
        String refreshToken = JwtUtils.generateToken(user, jwtProperties.refreshToken().secret(), -1);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse signUp(LoginRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(ZonedDateTime.now());

        UserEvent event = new UserEvent("CREATE", userMapper.modelToDto(user));

        boolean eventSent = userEventProducer.sendEvent(event);

        if (!eventSent) {
            throw new RuntimeException("Failed to send event to Kafka. User will not be saved.");
        }

        return saveUser(user);
    }

    private TokenResponse saveUser(User user) {
        User savedUser = userRepository.save(user);
        String refreshToken = JwtUtils.generateToken(savedUser, jwtProperties.refreshToken().secret(), -1);
        String accessToken = JwtUtils.generateToken(savedUser, jwtProperties.accessToken().secret(), jwtProperties.accessToken().ttl());
        return new TokenResponse(accessToken, refreshToken);
    }

    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::modelToDto);
    }

    public UserDto findById(@NotNull Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException(NOT_FOUND_USER.formatted(id)));
        return userMapper.modelToDto(user);
    }
    public UserDto updateUser(UpdateUserRequest updateUserRequest, Long userId) {
        updateUserRequest.validate();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.merge(updateUserRequest);

        User updatedUser = userRepository.save(user);

        return userMapper.modelToDto(updatedUser);
    }

}