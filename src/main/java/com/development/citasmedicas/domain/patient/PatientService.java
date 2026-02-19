package com.development.citasmedicas.domain.patient;

import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import com.development.citasmedicas.domain.patient.dto.CreatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatedPatientDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatePatientDTO;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientResponseDTO::new)
                .toList();
    }

    public PatientResponseDTO getPatientById(Long id) {
        var patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        return new PatientResponseDTO(patient);
    }

    @Transactional
    public PatientResponseDTO createPatient(CreatePatientDTO dto) {
        if(userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("El email ingresado ya existe");
        }
        User user = new User(dto.email(), passwordEncoder.encode(dto.password()), Role.ROLE_PATIENT);
        Patient patient = new Patient(dto.firstName(), dto.lastName(), dto.phoneNumber(), dto.birthDate(), user);

        patientRepository.save(patient);

        return new PatientResponseDTO(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        patient.getUser().setEnable(false);
    }

    @Transactional
    public UpdatedPatientDTO updatePatient(Long id, UpdatePatientDTO dto) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        if (dto.minimalModification()) {
            // Encriptar password si viene en el DTO
            String encodedPassword = null;
            if (dto.password() != null) {
                encodedPassword = passwordEncoder.encode(dto.password());
            }

            // Crear nuevo DTO con password encriptado
            UpdatePatientDTO dtoWithEncodedPassword = new UpdatePatientDTO(
                    dto.firstName(),
                    dto.lastName(),
                    dto.phoneNumber(),
                    dto.birthDate(),
                    dto.email(),
                    encodedPassword
            );

            patient.updatePatient(dtoWithEncodedPassword);

            return new UpdatedPatientDTO(patient);
        }

        throw new NoDataToUpdateException("No hay datos por modificar");
    }
}
