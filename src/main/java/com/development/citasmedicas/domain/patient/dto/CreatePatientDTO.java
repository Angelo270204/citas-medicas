package com.development.citasmedicas.domain.patient.dto;

import java.time.LocalDate;

public record CreatePatientDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,

        String email,
        String password
) {
}
