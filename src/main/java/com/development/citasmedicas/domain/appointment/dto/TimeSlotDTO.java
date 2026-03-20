package com.development.citasmedicas.domain.appointment.dto;

import java.time.LocalTime;

public record TimeSlotDTO(
        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {
}
