package com.aviatickets.profile.controller;

import com.aviatickets.profile.controller.request.LoginRequest;
import com.aviatickets.profile.controller.request.TokenRequest;
import com.aviatickets.profile.controller.response.AccessTokenResponse;
import com.aviatickets.profile.controller.response.RefreshTokenResponse;
import com.aviatickets.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/token")
    public AccessTokenResponse getNewAccessToken(@RequestBody TokenRequest tokenRequest) {
        String accessToken = userService.login(tokenRequest.refreshToken());
        return new AccessTokenResponse(accessToken);
    }

    @PostMapping("/login")
    public AccessTokenResponse login(@RequestBody LoginRequest request) {
        String accessToken = userService.login(request);
        return new AccessTokenResponse(accessToken);
    }

    @PostMapping("/signUp")
    public RefreshTokenResponse signUp(@RequestBody LoginRequest request) {
        return userService.signUp(request);
    }

}
