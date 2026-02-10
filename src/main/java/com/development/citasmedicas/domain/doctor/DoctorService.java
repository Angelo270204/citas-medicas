package com.development.citasmedicas.domain.doctor;

import com.development.citasmedicas.domain.doctor.dto.CreateDoctorDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.UpdateDoctorDTO;
import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorResponseDTO::new)
                .toList();
    }

    public DoctorResponseDTO getDoctorById(Long id) {
        var doctor = doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));
        return new DoctorResponseDTO(doctor);
    }

    @Transactional
    public DoctorResponseDTO createDoctor(CreateDoctorDTO dto) {
        User user = new User(dto.email(), dto.password(), Role.ROLE_DOCTOR);

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

        doctor.updateDoctor(dto);

        return new DoctorResponseDTO(doctor);
    }
}
