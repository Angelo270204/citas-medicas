package com.development.citasmedicas.domain.doctor;

import com.development.citasmedicas.domain.doctor.dto.CreateDoctorDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorAdminResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.UpdateDoctorDTO;
import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .filter(doctor -> doctor.getUser().isEnable())
                .map(DoctorResponseDTO::new)
                .toList();
    }

    public List<DoctorAdminResponseDTO> getAllDoctorsForAdmin() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorAdminResponseDTO::new)
                .toList();
    }

    public DoctorResponseDTO getDoctorById(Long id) {
        var doctor = doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));
        return new DoctorResponseDTO(doctor);
    }

    public DoctorAdminResponseDTO getDoctorByIdForAdmin(Long id) {
        var doctor = doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));
        return new DoctorAdminResponseDTO(doctor);
    }

    @Transactional
    public DoctorResponseDTO createDoctor(CreateDoctorDTO dto) {
        if(userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("El email ingresado ya existe");
        }

        User user = new User(dto.email(),passwordEncoder.encode(dto.password()), Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor(dto.firstName(), dto.lastName(), dto.cmp(), dto.specialty(), user);
        doctorRepository.save(doctor);

        return new DoctorResponseDTO(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        doctor.getUser().setEnable(false);
    }

    @Transactional
    public DoctorResponseDTO updateDoctor(Long id, UpdateDoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        if (!dto.minimalModification()) {
            throw new NoDataToUpdateException("No hay ningun dato por modificar");
        }

        // Encriptar password si viene en el DTO
        String encodedPassword = null;
        if (dto.password() != null) {
            encodedPassword = passwordEncoder.encode(dto.password());
        }

        // Crear nuevo DTO con password encriptado
        UpdateDoctorDTO dtoWithEncodedPassword = new UpdateDoctorDTO(
                dto.firstName(),
                dto.lastName(),
                dto.cmp(),
                dto.specialty(),
                dto.email(),
                encodedPassword
        );

        doctor.updateDoctor(dtoWithEncodedPassword);

        return new DoctorResponseDTO(doctor);
    }
}
