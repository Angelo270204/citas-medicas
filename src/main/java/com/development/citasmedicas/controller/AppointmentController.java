package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.appointment.AppointmentService;
import com.development.citasmedicas.domain.appointment.dto.AppointmentResponseDTO;
import com.development.citasmedicas.domain.appointment.dto.ScheduleAppointmentDTO;
import com.development.citasmedicas.domain.appointment.dto.ScheduleAppointmentPatientDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
        
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        
        List<AppointmentResponseDTO> appointments;
        
        if (isPatient) {
            appointments = service.getAppointmentsByPatientEmail(email);
        } else if (isDoctor) {
            appointments = service.getAppointmentsByDoctorEmail(email);
        } else {
            throw new IllegalArgumentException("Usuario no autorizado para ver citas");
        }

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        var appointments = service.getAllAppointments();

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        var appointment = service.getAppointmentById(id);

        return ResponseEntity.ok(appointment);
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> scheduledAppointment(
            @RequestBody @Valid ScheduleAppointmentPatientDTO dto,
            UriComponentsBuilder uriBuilder, Authentication authentication) {

        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (!isPatient) {
            throw new IllegalArgumentException(
                    "Use el endpoint /api/appointments/admin para crear citas de otros pacientes");
        }

        String email = authentication.getName();
        var app = service.scheduledAppointmentForPatient(dto, email);
        var url = uriBuilder.path("/api/appointments/{id}").buildAndExpand(app.id()).toUri();

        return ResponseEntity.created(url).body(app);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponseDTO> scheduledAppointmentAdmin(
            @RequestBody @Valid ScheduleAppointmentDTO dto, // <-- DTO original con patientId
            UriComponentsBuilder uriBuilder) {

        var app = service.scheduledAppointment(dto);
        var url = uriBuilder.path("/api/appointments/{id}").buildAndExpand(app.id()).toUri();

        return ResponseEntity.created(url).body(app);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        service.cancelAppointment(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(
            @PathVariable Long id,
            @RequestBody @Valid com.development.citasmedicas.domain.appointment.dto.CompleteAppointmentDTO dto) {
        var appointment = service.completeAppointment(id, dto.diagnosis());

        return ResponseEntity.ok(appointment);
    }
}
