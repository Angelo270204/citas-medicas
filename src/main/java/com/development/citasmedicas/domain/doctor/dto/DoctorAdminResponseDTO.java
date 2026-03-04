package com.development.citasmedicas.domain.doctor.dto;

import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.doctor.Specialty;

public record DoctorAdminResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String cmp,
        Specialty specialty,
        String email,
        boolean enable
) {
        public DoctorAdminResponseDTO(Doctor doctor){
                this(doctor.getId(),doctor.getFirstName(),doctor.getLastName(),doctor.getCmp(),doctor.getSpecialty(),doctor.getUser().getEmail(),doctor.getUser().isEnable());
        }
}
