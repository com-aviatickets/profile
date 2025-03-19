package com.aviatickets.profile.controller;


import com.aviatickets.profile.config.AppProperties.JwtProperties;
import com.aviatickets.profile.controller.request.UpdateUserRequest;
import com.aviatickets.profile.controller.response.UserDto;
import com.aviatickets.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.aviatickets.profile.controller.ControllerConstants.ACCESS_TOKEN_COOKIE;
import static com.aviatickets.profile.util.JwtUtils.getIdFromToken;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtProperties jwtProperties;
    private final UserService userService;

    @PatchMapping
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UpdateUserRequest updateUserRequest,
            @CookieValue(ACCESS_TOKEN_COOKIE) String accessToken
    ) {

        Long userId = getIdFromToken(accessToken, jwtProperties.accessToken().secret());

        UserDto updatedUser = userService.updateUser(updateUserRequest, userId);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(@CookieValue(ACCESS_TOKEN_COOKIE) String accessToken) {
        Long userId = getIdFromToken(accessToken, jwtProperties.accessToken().secret());
        UserDto userDto = userService.findById(userId);

        return ResponseEntity.ok(userDto);

    }
}
