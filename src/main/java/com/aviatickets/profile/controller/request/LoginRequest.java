package com.aviatickets.profile.controller.request;

public record LoginRequest (
        String username,
        String password
) {
}
