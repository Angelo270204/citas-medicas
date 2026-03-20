package com.development.citasmedicas.domain.appointment.dto;

import java.time.LocalDate;
import java.util.List;

public record AvailableSlotsResponseDTO(
        Long doctorId,
        String doctorName,
        LocalDate date,
        List<TimeSlotDTO> slots
) {
}
