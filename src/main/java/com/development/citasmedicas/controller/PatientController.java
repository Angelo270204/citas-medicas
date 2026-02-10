package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.patient.PatientService;
import com.development.citasmedicas.domain.patient.dto.CreatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatedPatientDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        PatientResponseDTO patient = patientService.getPatientById(id);

        return ResponseEntity.ok(patient);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody @Valid CreatePatientDTO dto, UriComponentsBuilder uriBuilder) {
        PatientResponseDTO patient = patientService.createPatient(dto);

        URI url = uriBuilder.path("/api/patients/{id}")
                .buildAndExpand(patient.id())
                .toUri();

        return ResponseEntity.created(url).body(patient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatedPatientDTO> updatePatient(@PathVariable Long id, @RequestBody UpdatePatientDTO dto) {
        var patient = patientService.updatePatient(id, dto);

        return ResponseEntity.ok(patient);
    }
}
