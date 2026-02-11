package com.development.citasmedicas.domain.appointment.dto;

import com.development.citasmedicas.domain.appointment.Appointment;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String reasonForVisit,
        String diagnosis,
        PatientResponseDTO patient,
        DoctorResponseDTO doctor
) {
    public AppointmentResponseDTO(Appointment app){
        this(app.getId(),app.getStartDateTime(),app.getEndDateTime(),app.getReasonForVisit(),app.getDiagnosis(),new PatientResponseDTO(app.getPatient()),new DoctorResponseDTO(app.getDoctor()));
    }
}