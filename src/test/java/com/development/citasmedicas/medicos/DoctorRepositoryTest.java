package com.development.citasmedicas.medicos;

import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.doctor.DoctorRepository;
import com.development.citasmedicas.domain.doctor.Specialty;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        doctorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar un doctor correctamente")
    void saveDoctorSuccessfully() {
        // ARRANGE
        User user = new User("doctor@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", 
                Specialty.GENERAL_MEDICINE, user);

        // ACT
        Doctor savedDoctor = doctorRepository.save(doctor);

        // ASSERT
        assertNotNull(savedDoctor.getId());
        assertEquals("Angelo", savedDoctor.getFirstName());
        assertEquals("CMP-123", savedDoctor.getCmp());
        assertEquals(Specialty.GENERAL_MEDICINE, savedDoctor.getSpecialty());
        assertEquals("doctor@mail.com", savedDoctor.getUser().getEmail());
    }

    @Test
    @DisplayName("Debe encontrar un doctor por ID")
    void findDoctorByIdSuccessfully() {
        // ARRANGE
        User user = new User("carlos@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Carlos", "Ramirez", "CMP-456", 
                Specialty.CARDIOLOGY, user);
        
        Doctor savedDoctor = doctorRepository.save(doctor);

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        // ASSERT
        assertTrue(foundDoctor.isPresent());
        assertEquals("Carlos", foundDoctor.get().getFirstName());
        assertEquals("CMP-456", foundDoctor.get().getCmp());
        assertEquals(Specialty.CARDIOLOGY, foundDoctor.get().getSpecialty());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el ID no existe")
    void findDoctorByIdNotFound() {
        // ARRANGE
        Long nonExistentId = 999L;

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findById(nonExistentId);

        // ASSERT
        assertFalse(foundDoctor.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar un doctor por email del usuario")
    void findDoctorByUserEmailSuccessfully() {
        // ARRANGE
        User user = new User("maria@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Maria", "Lopez", "CMP-789", 
                Specialty.PEDIATRICS, user);
        
        doctorRepository.save(doctor);

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findByUserEmail("maria@mail.com");

        // ASSERT
        assertTrue(foundDoctor.isPresent());
        assertEquals("Maria", foundDoctor.get().getFirstName());
        assertEquals("CMP-789", foundDoctor.get().getCmp());
        assertEquals("maria@mail.com", foundDoctor.get().getUser().getEmail());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el email no existe")
    void findDoctorByUserEmailNotFound() {
        // ARRANGE
        String nonExistentEmail = "noexiste@mail.com";

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findByUserEmail(nonExistentEmail);

        // ASSERT
        assertFalse(foundDoctor.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar un doctor por objeto User")
    void findDoctorByUserSuccessfully() {
        // ARRANGE
        User user = new User("pedro@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Pedro", "Martinez", "CMP-111", 
                Specialty.DERMATOLOGY, user);
        
        Doctor savedDoctor = doctorRepository.save(doctor);

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findByUser(savedDoctor.getUser());

        // ASSERT
        assertTrue(foundDoctor.isPresent());
        assertEquals("Pedro", foundDoctor.get().getFirstName());
        assertEquals(savedDoctor.getId(), foundDoctor.get().getId());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el usuario no existe")
    void findDoctorByUserNotFound() {
        // ARRANGE
        User userWithoutDoctor = new User("noexiste@mail.com", "password", Role.ROLE_DOCTOR);
        userWithoutDoctor = userRepository.save(userWithoutDoctor);

        // ACT
        Optional<Doctor> foundDoctor = doctorRepository.findByUser(userWithoutDoctor);

        // ASSERT
        assertFalse(foundDoctor.isPresent());
    }

    @Test
    @DisplayName("Debe obtener todos los doctores")
    void findAllDoctorsSuccessfully() {
        // ARRANGE
        User user1 = new User("doctor1@mail.com", "password123", Role.ROLE_DOCTOR);
        User user2 = new User("doctor2@mail.com", "password456", Role.ROLE_DOCTOR);
        
        Doctor doctor1 = new Doctor("Luis", "Fernandez", "CMP-222", 
                Specialty.CARDIOLOGY, user1);
        Doctor doctor2 = new Doctor("Ana", "Silva", "CMP-333", 
                Specialty.GYNECOLOGY, user2);
        
        doctorRepository.save(doctor1);
        doctorRepository.save(doctor2);

        // ACT
        List<Doctor> doctors = doctorRepository.findAll();

        // ASSERT
        assertFalse(doctors.isEmpty());
        assertTrue(doctors.size() >= 2);
    }

    @Test
    @DisplayName("Debe eliminar un doctor correctamente")
    void deleteDoctorSuccessfully() {
        // ARRANGE
        User user = new User("delete@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Delete", "Test", "CMP-000", 
                Specialty.GENERAL_MEDICINE, user);
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        Long doctorId = savedDoctor.getId();

        // ACT
        doctorRepository.deleteById(doctorId);

        // ASSERT
        Optional<Doctor> deletedDoctor = doctorRepository.findById(doctorId);
        assertFalse(deletedDoctor.isPresent());
    }

    @Test
    @DisplayName("Debe actualizar un doctor correctamente")
    void updateDoctorSuccessfully() {
        // ARRANGE
        User user = new User("update@mail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Original", "Name", "CMP-444", 
                Specialty.GENERAL_MEDICINE, user);
        
        Doctor savedDoctor = doctorRepository.save(doctor);

        // ACT
        savedDoctor.setFirstName("Updated");
        savedDoctor.setLastName("NewName");
        savedDoctor.setSpecialty(Specialty.CARDIOLOGY);
        Doctor updatedDoctor = doctorRepository.save(savedDoctor);

        // ASSERT
        assertEquals("Updated", updatedDoctor.getFirstName());
        assertEquals("NewName", updatedDoctor.getLastName());
        assertEquals(Specialty.CARDIOLOGY, updatedDoctor.getSpecialty());
        assertEquals(savedDoctor.getId(), updatedDoctor.getId());
    }

    @Test
    @DisplayName("Debe guardar doctores con diferentes especialidades")
    void saveDoctorsWithDifferentSpecialties() {
        // ARRANGE & ACT
        User user1 = new User("cardio@mail.com", "pass1", Role.ROLE_DOCTOR);
        User user2 = new User("neuro@mail.com", "pass2", Role.ROLE_DOCTOR);
        User user3 = new User("pediatra@mail.com", "pass3", Role.ROLE_DOCTOR);

        Doctor cardiologist = new Doctor("Juan", "Cardio", "CMP-CAR", 
                Specialty.CARDIOLOGY, user1);
        Doctor dermatologist = new Doctor("Ana", "Derm", "CMP-DER", 
                Specialty.DERMATOLOGY, user2);
        Doctor pediatrician = new Doctor("Luis", "Pedia", "CMP-PED", 
                Specialty.PEDIATRICS, user3);

        doctorRepository.save(cardiologist);
        doctorRepository.save(dermatologist);
        doctorRepository.save(pediatrician);

        // ASSERT
        List<Doctor> allDoctors = doctorRepository.findAll();
        assertTrue(allDoctors.size() >= 3);
        
        assertTrue(allDoctors.stream()
                .anyMatch(d -> d.getSpecialty() == Specialty.CARDIOLOGY));
        assertTrue(allDoctors.stream()
                .anyMatch(d -> d.getSpecialty() == Specialty.DERMATOLOGY));
        assertTrue(allDoctors.stream()
                .anyMatch(d -> d.getSpecialty() == Specialty.PEDIATRICS));
    }
}
