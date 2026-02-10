package com.development.citasmedicas.domain.appointment.exception;

public class InvalidAppointmentTimeRangeException extends RuntimeException {
    public InvalidAppointmentTimeRangeException(String message) {
        super(message);
    }
}
