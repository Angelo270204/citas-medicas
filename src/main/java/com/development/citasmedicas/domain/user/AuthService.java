package com.development.citasmedicas.domain.user;

import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.doctor.DoctorRepository;
import com.development.citasmedicas.domain.patient.Patient;
import com.development.citasmedicas.domain.patient.PatientRepository;
import com.development.citasmedicas.domain.user.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AuthService(UserRepository userRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new SecurityUser(user);
    }

    public UserResponse getUserResponseFromUser(User user) {
        if (user.getRole() == Role.ROLE_DOCTOR) {
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado"));
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    user.getRole()
            );
        } else if (user.getRole() == Role.ROLE_PATIENT) {
            Patient patient = patientRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    user.getRole()
            );
        }
        
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                null,
                null,
                user.getRole()
        );
    }
}
