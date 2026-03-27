package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.doctor.DoctorService;
import com.development.citasmedicas.infra.security.JwtAuthorizationFilter;
import com.development.citasmedicas.infra.security.JwtUtil;
import com.development.citasmedicas.infra.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, DoctorControllerSecurityTest.TestSecurityBeans.class})
class DoctorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    @DisplayName("GET /api/doctors/admin retorna 403 si no hay autenticacion")
    void getAllDoctorsForAdminReturnsForbiddenWhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/doctors/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    @DisplayName("GET /api/doctors/admin retorna 403 si el rol no es ADMIN")
    void getAllDoctorsForAdminReturnsForbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/doctors/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/doctors/admin retorna 200 si el rol es ADMIN")
    void getAllDoctorsForAdminReturnsOkWhenAdmin() throws Exception {
        mockMvc.perform(get("/api/doctors/admin"))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class TestSecurityBeans {
        @Bean
        JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }
    }
}
