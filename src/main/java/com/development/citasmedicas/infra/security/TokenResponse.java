package com.development.citasmedicas.infra.security;

import com.development.citasmedicas.domain.user.dto.UserResponse;

public record TokenResponse(
        String token,
        UserResponse user
) {
    public TokenResponse(String token) {
        this(token, null);
    }
}
