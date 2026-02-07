package com.development.citasmedicas.domain.patient;

import com.development.citasmedicas.domain.patient.dto.CreatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatedPatientDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatePatientDTO;
import com.development.citasmedicas.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository=patientRepository;
    }

    public List<Patient> getAllPatients(){
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id){
        return patientRepository.findById(id).orElseThrow(()->new EntityNotFoundException("El id ingresado no existe"));
    }

    @Transactional
    public PatientResponseDTO createPatient(CreatePatientDTO dto){
        User user = new User(dto.email(),dto.password());
        Patient patient = new Patient(dto.firstName(),dto.lastName(), dto.phoneNumber(), dto.birthDate(),user);

        patientRepository.save(patient);

        return new PatientResponseDTO(patient);
    }

    @Transactional
    public void deletePatient(Long id){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new EntityNotFoundException("El id ingresado no existe"));

        patient.getUser().setEnable(false);
    }

    @Transactional
    public UpdatedPatientDTO updatePatient(Long id, UpdatePatientDTO dto){
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("El id ingresado no existe"));

        patient.updatePatient(dto);

        return new UpdatedPatientDTO(patient);
    }
}
