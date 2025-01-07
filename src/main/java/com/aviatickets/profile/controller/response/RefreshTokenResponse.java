package com.aviatickets.profile.controller.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
