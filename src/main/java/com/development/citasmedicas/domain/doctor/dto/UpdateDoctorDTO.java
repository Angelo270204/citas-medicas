package com.development.citasmedicas.domain.doctor.dto;

import com.development.citasmedicas.domain.doctor.Specialty;

public record UpdateDoctorDTO(
        String firstName,
        String lastName,
        String cmp,
        Specialty specialty,
        String email,
        String password
) {
    public boolean minimalModification() {
        return firstName != null || lastName != null || cmp != null || specialty != null || email != null || password != null;
    }
}