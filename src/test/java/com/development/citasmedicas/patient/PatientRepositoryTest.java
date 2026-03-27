package com.development.citasmedicas.patient;

import com.development.citasmedicas.domain.patient.Patient;
import com.development.citasmedicas.domain.patient.PatientRepository;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        patientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar un paciente correctamente")
    void savePatientSuccessfully() {
        // ARRANGE
        User user = new User("test@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234", 
                LocalDate.of(1990, 5, 15), user);

        // ACT
        Patient savedPatient = patientRepository.save(patient);

        // ASSERT
        assertNotNull(savedPatient.getId());
        assertEquals("Juan", savedPatient.getFirstName());
        assertEquals("test@mail.com", savedPatient.getUser().getEmail());
    }

    @Test
    @DisplayName("Debe encontrar un paciente por ID")
    void findPatientByIdSuccessfully() {
        // ARRANGE
        User user = new User("juan@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234", 
                LocalDate.of(1990, 5, 15), user);
        
        Patient savedPatient = patientRepository.save(patient);

        // ACT
        Optional<Patient> foundPatient = patientRepository.findById(savedPatient.getId());

        // ASSERT
        assertTrue(foundPatient.isPresent());
        assertEquals("Juan", foundPatient.get().getFirstName());
        assertEquals("juan@mail.com", foundPatient.get().getUser().getEmail());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el ID no existe")
    void findPatientByIdNotFound() {
        // ARRANGE
        Long nonExistentId = 999L;

        // ACT
        Optional<Patient> foundPatient = patientRepository.findById(nonExistentId);

        // ASSERT
        assertFalse(foundPatient.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar un paciente por email del usuario")
    void findPatientByUserEmailSuccessfully() {
        // ARRANGE
        User user = new User("maria@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Maria", "Lopez", "555-5678", 
                LocalDate.of(1985, 8, 20), user);
        
        patientRepository.save(patient);

        // ACT
        Optional<Patient> foundPatient = patientRepository.findByUserEmail("maria@mail.com");

        // ASSERT
        assertTrue(foundPatient.isPresent());
        assertEquals("Maria", foundPatient.get().getFirstName());
        assertEquals("maria@mail.com", foundPatient.get().getUser().getEmail());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el email no existe")
    void findPatientByUserEmailNotFound() {
        // ARRANGE
        String nonExistentEmail = "noexiste@mail.com";

        // ACT
        Optional<Patient> foundPatient = patientRepository.findByUserEmail(nonExistentEmail);

        // ASSERT
        assertFalse(foundPatient.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar un paciente por objeto User")
    void findPatientByUserSuccessfully() {
        // ARRANGE
        User user = new User("carlos@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Carlos", "Gomez", "555-9999", 
                LocalDate.of(1995, 3, 10), user);
        
        Patient savedPatient = patientRepository.save(patient);

        // ACT
        Optional<Patient> foundPatient = patientRepository.findByUser(savedPatient.getUser());

        // ASSERT
        assertTrue(foundPatient.isPresent());
        assertEquals("Carlos", foundPatient.get().getFirstName());
        assertEquals(savedPatient.getId(), foundPatient.get().getId());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty cuando el usuario no existe")
    void findPatientByUserNotFound() {
        // ARRANGE
        User userWithoutPatient = new User("noexiste@mail.com", "password", Role.ROLE_PATIENT);
        userWithoutPatient = userRepository.save(userWithoutPatient);

        // ACT
        Optional<Patient> foundPatient = patientRepository.findByUser(userWithoutPatient);

        // ASSERT
        assertFalse(foundPatient.isPresent());
    }

    @Test
    @DisplayName("Debe obtener todos los pacientes")
    void findAllPatientsSuccessfully() {
        // ARRANGE
        User user1 = new User("patient1@mail.com", "password123", Role.ROLE_PATIENT);
        User user2 = new User("patient2@mail.com", "password456", Role.ROLE_PATIENT);
        
        Patient patient1 = new Patient("Pedro", "Martinez", "555-1111", 
                LocalDate.of(1992, 1, 1), user1);
        Patient patient2 = new Patient("Ana", "Silva", "555-2222", 
                LocalDate.of(1988, 6, 15), user2);
        
        patientRepository.save(patient1);
        patientRepository.save(patient2);

        // ACT
        List<Patient> patients = patientRepository.findAll();

        // ASSERT
        assertFalse(patients.isEmpty());
        assertTrue(patients.size() >= 2);
    }

    @Test
    @DisplayName("Debe eliminar un paciente correctamente")
    void deletePatientSuccessfully() {
        // ARRANGE
        User user = new User("delete@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Delete", "Test", "555-0000", 
                LocalDate.of(1990, 1, 1), user);
        
        Patient savedPatient = patientRepository.save(patient);
        Long patientId = savedPatient.getId();

        // ACT
        patientRepository.deleteById(patientId);

        // ASSERT
        Optional<Patient> deletedPatient = patientRepository.findById(patientId);
        assertFalse(deletedPatient.isPresent());
    }

    @Test
    @DisplayName("Debe actualizar un paciente correctamente")
    void updatePatientSuccessfully() {
        // ARRANGE
        User user = new User("update@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Original", "Name", "555-0000", 
                LocalDate.of(1990, 1, 1), user);
        
        Patient savedPatient = patientRepository.save(patient);

        // ACT
        savedPatient.setFirstName("Updated");
        savedPatient.setLastName("NewName");
        Patient updatedPatient = patientRepository.save(savedPatient);

        // ASSERT
        assertEquals("Updated", updatedPatient.getFirstName());
        assertEquals("NewName", updatedPatient.getLastName());
        assertEquals(savedPatient.getId(), updatedPatient.getId());
    }
}
