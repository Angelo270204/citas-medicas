package com.development.citasmedicas.infra.exception;

import com.development.citasmedicas.domain.appointment.exception.AppointmentConflictException;
import com.development.citasmedicas.domain.exception.ErrorData;
import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoDataToUpdateException.class)
    public ResponseEntity<ErrorData> noDataToUpdate(NoDataToUpdateException ex) {
        return ResponseEntity.badRequest().body(new ErrorData(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorData> entityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorData(ex.getMessage()));
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ErrorData> appointmentConflict(AppointmentConflictException ex) {
        return ResponseEntity.badRequest().body(new ErrorData(ex.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorData> expiredToken(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorData("Token expirado"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorData> invalidSignature(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorData("Token inválido"));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorData> malformedToken(MalformedJwtException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorData("Token mal formado"));
    }
}
