package com.development.citasmedicas.domain.patient.dto;

import java.time.LocalDate;

public record UpdatePatientDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,

        String email,
        String password
) {
    public boolean minimalModification() {
        return firstName != null || lastName != null || phoneNumber != null || birthDate != null || email != null || password != null;
    }
}
