package com.development.citasmedicas.domain.user.dto;

import com.development.citasmedicas.domain.user.Role;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
