package com.development.citasmedicas.domain.appointment.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScheduleAppointmentPatientDTO(
        @NotNull @Future LocalDateTime startDateTime,
        @NotNull LocalDateTime endDateTime,
        @NotBlank String reasonForVisit,
        @NotNull Long doctorId
) {
}