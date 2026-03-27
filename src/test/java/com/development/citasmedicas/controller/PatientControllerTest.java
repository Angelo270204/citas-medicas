package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.patient.PatientService;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.infra.exception.GlobalExceptionHandler;
import com.development.citasmedicas.infra.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        clearInvocations(patientService);
    }

    @Test
    @DisplayName("GET /api/patients retorna lista")
    void getAllPatientsReturnsList() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(
                new PatientResponseDTO(1L, "Juan", "Perez", "555-1234", LocalDate.of(1990, 5, 15), "juan@mail.com"),
                new PatientResponseDTO(2L, "Maria", "Lopez", "555-5678", LocalDate.of(1985, 8, 20), "maria@mail.com")
        ));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Juan"))
                .andExpect(jsonPath("$[1].firstName").value("Maria"));

        verify(patientService).getAllPatients();
    }

    @Test
    @DisplayName("GET /api/patients/{id} retorna un paciente")
    void getPatientByIdReturnsPatientWhenExists() throws Exception {
        long id = 10L;
        when(patientService.getPatientById(id)).thenReturn(
                new PatientResponseDTO(id, "Juan", "Perez", "555-1234", LocalDate.of(1990, 5, 15), "juan@mail.com")
        );

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"));

        verify(patientService).getPatientById(id);
    }

    @Test
    @DisplayName("GET /api/patients/{id} retorna 404 cuando no existe")
    void getPatientByIdReturnsNotFoundWhenNotExists() throws Exception {
        long id = 999L;
        when(patientService.getPatientById(id)).thenThrow(new EntityNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isNotFound());

        verify(patientService).getPatientById(id);
    }



    @Test
    @DisplayName("DELETE /api/patients/{id} retorna 204 (basico)")
    void deletePatientReturnsNoContentBasic() throws Exception {
        long id = 12L;
        doNothing().when(patientService).deletePatient(id);

        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatient(id);
    }

}
