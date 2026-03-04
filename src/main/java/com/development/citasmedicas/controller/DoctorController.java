package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.doctor.DoctorService;
import com.development.citasmedicas.domain.doctor.dto.CreateDoctorDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorAdminResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.UpdateDoctorDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        var doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorAdminResponseDTO>> getAllDoctorsForAdmin() {
        var doctors = doctorService.getAllDoctorsForAdmin();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        var doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorAdminResponseDTO> getDoctorByIdForAdmin(@PathVariable Long id) {
        var doctor = doctorService.getDoctorByIdForAdmin(id);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> createDoctor(@RequestBody @Valid CreateDoctorDTO dto, UriComponentsBuilder uriBuilder) {
        DoctorResponseDTO doctor = doctorService.createDoctor(dto);

        var url = uriBuilder.path("/api/doctors/{id}").buildAndExpand(doctor.id()).toUri();

        return ResponseEntity.created(url).body(doctor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable Long id, @RequestBody UpdateDoctorDTO dto) {
        DoctorResponseDTO doctor = doctorService.updateDoctor(id, dto);

        return ResponseEntity.ok(doctor);
    }
}
