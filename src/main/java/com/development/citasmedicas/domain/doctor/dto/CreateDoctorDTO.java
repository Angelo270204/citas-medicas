package com.development.citasmedicas.domain.doctor.dto;

import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.doctor.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Doctor}
 */
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
        @NotBlank(message = "La password es obligatoria")
        String password
        ) {
}