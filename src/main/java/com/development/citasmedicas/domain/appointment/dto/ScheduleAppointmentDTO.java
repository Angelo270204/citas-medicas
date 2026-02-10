package com.development.citasmedicas.domain.appointment.dto;

import com.development.citasmedicas.domain.appointment.Appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleAppointmentDTO(
        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        @Future(message = "La fecha de inicio debe ser en el futuro")
        LocalDateTime startDateTime,
        @NotNull(message = "La fecha y hora de fin es obligatoria")
        LocalDateTime endDateTime,
        @NotBlank(message = "El motivo de la visita es obligatorio")
        String reasonForVisit,
        @NotNull(message = "El ID del doctor es obligatorio")
        Long doctorId,
        @NotNull(message = "El ID del paciente es obligatorio")
        Long patientId
) {
    public ScheduleAppointmentDTO(Appointment app){
        this(app.getStartDateTime(),app.getEndDateTime(),app.getReasonForVisit(),app.getDoctor().getId(), app.getPatient().getId());
    }
}
