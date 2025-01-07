package com.aviatickets.profile.controller.response;

public record ErrorDto(
        String message,
        String status,
        int code
) {
}
