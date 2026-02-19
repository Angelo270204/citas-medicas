package com.development.citasmedicas.domain.doctor.dto;

import com.development.citasmedicas.domain.doctor.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDoctorDTO(
        @NotBlank(message = "El nombre del medico es obligatorio")
        String firstName,
        @NotBlank(message = "Los apellidos del medico son obligatorios")
        String lastName,
        @NotBlank(message = "El cmp es obligatorio")
        String cmp,
        @NotNull(message = "La especialidad del medico es obligatioria")
        Specialty specialty,
        @NotBlank(message = "El correo electronico es obligatorio")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "La contraseña debe contener letras y números")
        String password
) {
}