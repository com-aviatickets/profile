package com.aviatickets.profile.dto;

import java.time.ZonedDateTime;

public record CustomPrincipal(
        Long userId,
        ZonedDateTime lastLogin
) {
}
