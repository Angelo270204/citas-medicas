package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.appointment.AppointmentService;
import com.development.citasmedicas.domain.appointment.dto.AppointmentResponseDTO;
import com.development.citasmedicas.domain.appointment.dto.ScheduleAppointmentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AppointmentResponseDTO> scheduledAppointment(@RequestBody @Valid ScheduleAppointmentDTO dto, UriComponentsBuilder uriBuilder) {
        var app = service.scheduledAppointment(dto);

        var url = uriBuilder.path("/api/appointments/{id}").buildAndExpand(app.id()).toUri();

        return ResponseEntity.created(url).body(app);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id){
        service.cancelAppointment(id);

        return ResponseEntity.noContent().build();
    }
}
