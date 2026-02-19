package com.development.citasmedicas.controller;

import com.development.citasmedicas.domain.patient.PatientService;
import com.development.citasmedicas.domain.patient.dto.CreatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.domain.user.SecurityUser;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.infra.security.JwtUtil;
import com.development.citasmedicas.infra.security.LoginRequestDTO;
import com.development.citasmedicas.infra.security.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PatientService patientService;

    public AuthController( JwtUtil jwtUtil, AuthenticationManager authManager, PatientService patientService) {
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.patientService = patientService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> userLogin(@Valid @RequestBody LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        dto.username(),
                        dto.password()
                );

        Authentication authentication = authManager.authenticate(authToken);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> userRegister(@Valid @RequestBody CreatePatientDTO dto, UriComponentsBuilder uriBulder){
        PatientResponseDTO patient = patientService.createPatient(dto);
        var url = uriBulder.path("/api/patients/{id}").buildAndExpand(patient.id()).toUri();

        String token = jwtUtil.generateToken(patient.email(), "ROLE_PATIENT");

        return ResponseEntity.created(url).body(new TokenResponse(token));
    }
}
