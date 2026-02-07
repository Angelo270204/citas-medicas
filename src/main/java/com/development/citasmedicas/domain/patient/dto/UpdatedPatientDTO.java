package com.development.citasmedicas.domain.patient.dto;

import com.development.citasmedicas.domain.patient.Patient;
import com.development.citasmedicas.domain.user.Role;

import java.time.LocalDate;

public record UpdatedPatientDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        String email,
        Role role
) {
    public UpdatedPatientDTO(Patient pat){
        this(pat.getFirstName(),pat.getLastName(),pat.getPhoneNumber(),pat.getBirthDate(),pat.getUser().getEmail(),pat.getUser().getRole());
    }
}
