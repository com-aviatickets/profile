package com.aviatickets.profile.controller.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
