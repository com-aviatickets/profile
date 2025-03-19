package com.aviatickets.profile.controller;

import com.aviatickets.profile.controller.request.LoginRequest;
import com.aviatickets.profile.controller.response.TokenResponse;
import com.aviatickets.profile.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.aviatickets.profile.controller.ControllerConstants.ACCESS_TOKEN_COOKIE;
import static com.aviatickets.profile.controller.ControllerConstants.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping( "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    public static final int MAX_AGE_COOKIE = 31557600;
    private final UserService userService;

    @GetMapping("/refresh")
    public void getNewAccessToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        String accessToken = userService.login(refreshToken);

        setCookie(response, refreshToken, accessToken, MAX_AGE_COOKIE);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(request);

        setCookie(response, tokenResponse.refreshToken(), tokenResponse.accessToken(), MAX_AGE_COOKIE);
    }

    @PostMapping("/signUp")
    public void signUp(@RequestBody LoginRequest request,  HttpServletResponse response) {
        if (request == null) {
            throw new IllegalArgumentException("LoginRequest cannot be null");
        }
        TokenResponse refreshToken = userService.signUp(request);
        setCookie(response, refreshToken.refreshToken(), refreshToken.accessToken(), MAX_AGE_COOKIE);

        response.setHeader("Refresh-Token", refreshToken.refreshToken());
        response.setHeader("Access-Token", refreshToken.accessToken());
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        setCookie(response, null, null, 0);
    }

    private void setCookie(HttpServletResponse response, String refreshToken, String accessToken, int maxAge) {
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(maxAge); // 1 час

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge); // 7 дней

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}
