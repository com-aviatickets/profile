package com.aviatickets.profile.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(JwtProperties jwt) {

    @ConfigurationProperties(prefix = "app.jwt")
    public record JwtProperties(
            AccessTokenProperties accessToken,
            RefreshTokenProperties refreshToken
    ) {

        public record AccessTokenProperties(
                String secret,
                long ttl
        ) {
        }

        public record RefreshTokenProperties(String secret) {
        }

    }

}
