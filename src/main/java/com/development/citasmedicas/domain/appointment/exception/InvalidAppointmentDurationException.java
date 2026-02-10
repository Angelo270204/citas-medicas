package com.development.citasmedicas.domain.appointment.exception;

public class InvalidAppointmentDurationException extends RuntimeException {
    public InvalidAppointmentDurationException(String message) {
        super(message);
    }
}
