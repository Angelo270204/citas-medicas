package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.doctor.DoctorService;
import com.development.citasmedicas.domain.doctor.Specialty;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.infra.exception.GlobalExceptionHandler;
import com.development.citasmedicas.infra.security.JwtUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DoctorControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        clearInvocations(doctorService);
    }

    @Test
    @DisplayName("GET /api/doctors retorna lista")
    void getAllDoctorsReturnsList() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(List.of(
                new DoctorResponseDTO(1L, "Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, "doctor1@mail.com"),
                new DoctorResponseDTO(2L, "Carlos", "Ramirez", "CMP-456", Specialty.CARDIOLOGY, "doctor2@mail.com")
        ));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Angelo"))
                .andExpect(jsonPath("$[1].firstName").value("Carlos"));

        verify(doctorService).getAllDoctors();
    }

    @Test
    @DisplayName("GET /api/doctors/{id} retorna un doctor")
    void getDoctorByIdReturnsDoctorWhenExists() throws Exception {
        long id = 10L;

        when(doctorService.getDoctorById(id)).thenReturn(
                new DoctorResponseDTO(id, "Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, "doctor@mail.com")
        );

        mockMvc.perform(get("/api/doctors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Angelo"))
                .andExpect(jsonPath("$.cmp").value("CMP-123"));

        verify(doctorService).getDoctorById(id);
    }

    @Test
    @DisplayName("GET /api/doctors/{id} retorna 404 cuando no existe")
    void getDoctorByIdReturnsNotFoundWhenNotExists() throws Exception {
        long id = 999L;

        when(doctorService.getDoctorById(id))
                .thenThrow(new EntityNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/doctors/{id}", id))
                .andExpect(status().isNotFound());

        verify(doctorService).getDoctorById(id);
    }

    @Test
    @DisplayName("POST /api/doctors retorna 201")
    void createDoctorReturnsCreated() throws Exception {
        when(doctorService.createDoctor(any())).thenReturn(
                new DoctorResponseDTO(99L, "Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, "newdoctor@mail.com")
        );

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "Angelo",
                          "lastName": "Rubinos",
                          "cmp": "CMP-123",
                          "specialty": "GENERAL_MEDICINE",
                          "email": "newdoctor@mail.com",
                          "password": "pass1234"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/doctors/99")))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.email").value("newdoctor@mail.com"));

        verify(doctorService).createDoctor(any());
    }

    @Test
    @DisplayName("GET /api/doctors/admin retorna 200")
    void getAllDoctorsForAdminReturnsOkWhenFiltersOff() throws Exception {
        mockMvc.perform(get("/api/doctors/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/doctors/{id} retorna 200")
    void updateDoctorReturnsOk() throws Exception {
        long id = 20L;

        when(doctorService.updateDoctor(eq(id), any())).thenReturn(
                new DoctorResponseDTO(id, "Carlos", "Ramirez", "CMP-000", Specialty.CARDIOLOGY, "carlos@mail.com")
        );

        mockMvc.perform(put("/api/doctors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "Carlos",
                          "lastName": "Ramirez",
                          "specialty": "CARDIOLOGY",
                          "email": "carlos@mail.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@mail.com"));

        verify(doctorService).updateDoctor(eq(id), any());
    }

    @Test
    @DisplayName("DELETE /api/doctors/{id} retorna 204")
    void deleteDoctorReturnsNoContent() throws Exception {
        long id = 30L;

        doNothing().when(doctorService).deleteDoctor(id);

        mockMvc.perform(delete("/api/doctors/{id}", id))
                .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(id);
    }
}
