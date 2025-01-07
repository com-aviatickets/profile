package com.aviatickets.profile.controller.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String refreshToken) {

}
