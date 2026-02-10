package com.development.citasmedicas.domain.patient.dto;

import com.development.citasmedicas.domain.patient.Patient;

import java.time.LocalDate;

public record PatientResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        String email
) {
    public PatientResponseDTO(Patient pat){
        this(pat.getId(),pat.getFirstName(),pat.getLastName(),pat.getPhoneNumber(),pat.getBirthDate(),pat.getUser().getEmail());
    }
}
