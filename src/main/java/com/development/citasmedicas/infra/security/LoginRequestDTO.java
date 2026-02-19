package com.development.citasmedicas.infra.security;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El correo electronico es obligatorio")
        String username,
        @NotBlank(message = "La password es obligatoria")
        String password
) {
}
