package com.development.citasmedicas.domain.appointment.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteAppointmentDTO(
        @NotBlank(message = "El diagnóstico es obligatorio")
        String diagnosis
) {
}
