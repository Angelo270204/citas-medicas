package com.development.citasmedicas.domain.patient.dto;

import com.development.citasmedicas.domain.patient.Patient;

import java.time.LocalDate;

public record PatientResponseDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        Long user_id
) {
    public PatientResponseDTO(Patient pat){
        this(pat.getFirstName(),pat.getLastName(),pat.getPhoneNumber(),pat.getBirthDate(),pat.getUser().getId());
    }
}
