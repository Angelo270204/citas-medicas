package com.development.citasmedicas.domain.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePatientDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        @NotBlank(message = "Los apellidos son obligatorios")
        String lastName,
        @NotBlank(message = "El numero telefonico es obligatorio")
        String phoneNumber,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate birthDate,
        @Email(message = "El email es obligatorio y debe tener un dominio")
        String email,
        @NotBlank(message = "La contrasena es obigatoria")
        String password
) {
}
