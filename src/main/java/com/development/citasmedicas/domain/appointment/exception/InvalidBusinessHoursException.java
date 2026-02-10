package com.development.citasmedicas.domain.appointment.exception;

public class InvalidBusinessHoursException extends RuntimeException {
    public InvalidBusinessHoursException(String message) {
        super(message);
    }
}
