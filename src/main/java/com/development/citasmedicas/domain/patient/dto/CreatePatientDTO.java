package com.development.citasmedicas.domain.patient.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreatePatientDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        @NotBlank(message = "Los apellidos son obligatorios")
        String lastName,
        @NotBlank(message = "El numero telefonico es obligatorio")
        String phoneNumber,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate birthDate,
        @Email(message = "El email es obligatorio y debe tener un dominio")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "La contraseña debe contener letras y números")
        String password
) {
}
