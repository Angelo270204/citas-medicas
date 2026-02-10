package com.development.citasmedicas.domain.appointment.dto;

import com.development.citasmedicas.domain.appointment.Appointment;
import com.development.citasmedicas.domain.appointment.AppointmentStatus;
import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.patient.Patient;

import java.time.LocalDateTime;

public record AppointmentAdminResponseDTO(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String reasonForVisit,
        AppointmentStatus status,
        String diagnosis,
        Doctor doctor,
        Patient patient
) {
    public AppointmentAdminResponseDTO(Appointment app){
        this(app.getId(),app.getStartDateTime(),app.getEndDateTime(),app.getReasonForVisit(),app.getStatus(),app.getDiagnosis(),app.getDoctor(),app.getPatient());
    }
}