package com.development.citasmedicas.patient;

import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import com.development.citasmedicas.domain.patient.Patient;
import com.development.citasmedicas.domain.patient.PatientRepository;
import com.development.citasmedicas.domain.patient.PatientService;
import com.development.citasmedicas.domain.patient.dto.CreatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.PatientResponseDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatePatientDTO;
import com.development.citasmedicas.domain.patient.dto.UpdatedPatientDTO;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    @DisplayName("Debe retornar lista de pacientes cuando existen pacientes")
    void getAllPatientsReturnsListWhenPatientsExist() {
        // ARRANGE
        User user1 = new User("juan@mail.com", "password123", Role.ROLE_PATIENT);
        User user2 = new User("maria@mail.com", "password456", Role.ROLE_PATIENT);

        Patient patient1 = new Patient("Juan", "Perez", "555-1234", LocalDate.of(1990, 5, 15), user1);
        Patient patient2 = new Patient("Maria", "Lopez", "555-5678", LocalDate.of(1985, 8, 20), user2);

        List<Patient> patients = List.of(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(patients);

        // ACT
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).firstName());
        assertEquals("Maria", result.get(1).firstName());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no existen pacientes")
    void getAllPatientsReturnsEmptyListWhenNoPatientsExist() {
        // ARRANGE
        when(patientRepository.findAll()).thenReturn(List.of());

        // ACT
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar paciente cuando el ID existe")
    void getPatientByIdReturnsPatientWhenIdExists() {
        // ARRANGE
        Long patientId = 1L;
        User user = new User("juan@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234",
                LocalDate.of(1990, 5, 15), user);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // ACT
        PatientResponseDTO result = patientService.getPatientById(patientId);

        // ASSERT
        assertNotNull(result);
        assertEquals("Juan", result.firstName());
        assertEquals("juan@mail.com", result.email());
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el ID no existe")
    void getPatientByIdThrowsExceptionWhenIdNotExists() {
        // ARRANGE
        Long nonExistentId = 999L;
        when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT (se combinan cuando testeamos excepciones)
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.getPatientById(nonExistentId)
        );

        assertEquals("El id ingresado no existe", exception.getMessage());
    }

    // ========== Tests para createPatient() ==========

    @Test
    @DisplayName("Debe crear un paciente exitosamente con password encriptado")
    void createPatientSuccessfully() {
        // ARRANGE
        CreatePatientDTO dto = new CreatePatientDTO(
                "Carlos",
                "Gomez",
                "555-9999",
                LocalDate.of(1995, 3, 10),
                "carlos@mail.com",
                "plainPassword123"
        );

        // Simular que el email NO existe
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        
        // Simular la encriptación del password
        when(passwordEncoder.encode(dto.password())).thenReturn("encryptedPassword123");

        // Simular que save() retorna el patient (aunque en realidad no retorna nada, 
        // pero necesitamos que el patient tenga los datos correctos)
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        PatientResponseDTO result = patientService.createPatient(dto);

        // ASSERT
        assertNotNull(result);
        assertEquals("Carlos", result.firstName());
        assertEquals("carlos@mail.com", result.email());
        
        // Verificar que se llamaron los métodos esperados
        verify(userRepository).existsByEmail(dto.email());
        verify(passwordEncoder).encode(dto.password());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void createPatientThrowsExceptionWhenEmailExists() {
        // ARRANGE
        CreatePatientDTO dto = new CreatePatientDTO(
                "Carlos",
                "Gomez",
                "555-9999",
                LocalDate.of(1995, 3, 10),
                "carlos@mail.com",
                "plainPassword123"
        );

        // Simular que el email YA EXISTE
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.createPatient(dto)
        );

        assertEquals("El email ingresado ya existe", exception.getMessage());
        
        // Verificar que NO se intentó encriptar el password ni guardar el paciente
        verify(userRepository).existsByEmail(dto.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    // ========== Tests para deletePatient() ==========

    @Test
    @DisplayName("Debe deshabilitar el usuario del paciente cuando se elimina exitosamente")
    void deletePatientSuccessfully() {
        // ARRANGE
        Long patientId = 1L;
        User user = new User("juan@mail.com", "password123", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234",
                LocalDate.of(1990, 5, 15), user);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // ACT
        patientService.deletePatient(patientId);

        // ASSERT
        // Verificar que el usuario fue deshabilitado
        assertFalse(user.isEnable());
        verify(patientRepository).findById(patientId);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el ID no existe al eliminar")
    void deletePatientThrowsExceptionWhenIdNotExists() {
        // ARRANGE
        Long nonExistentId = 999L;
        when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.deletePatient(nonExistentId)
        );

        assertEquals("El id ingresado no existe", exception.getMessage());
        verify(patientRepository).findById(nonExistentId);
    }

    // ========== Tests para updatePatient() ==========

    @Test
    @DisplayName("Debe actualizar el paciente exitosamente cuando hay datos válidos")
    void updatePatientSuccessfully() {
        // ARRANGE
        Long patientId = 1L;
        User user = new User("juan@mail.com", "oldPassword", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234",
                LocalDate.of(1990, 5, 15), user);

        UpdatePatientDTO updateDTO = new UpdatePatientDTO(
                "Carlos",
                "Gomez",
                "555-9999",
                LocalDate.of(1995, 3, 10),
                "carlos@mail.com",
                "newPassword123"
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encryptedNewPassword");

        // ACT
        UpdatedPatientDTO result = patientService.updatePatient(patientId, updateDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals("Carlos", result.firstName());
        assertEquals("Gomez", result.lastName());
        assertEquals("555-9999", result.phoneNumber());
        assertEquals("carlos@mail.com", result.email());
        
        verify(patientRepository).findById(patientId);
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    @DisplayName("Debe actualizar solo los campos proporcionados")
    void updatePatientPartially() {
        // ARRANGE
        Long patientId = 1L;
        User user = new User("juan@mail.com", "oldPassword", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234",
                LocalDate.of(1990, 5, 15), user);

        // Solo actualizar firstName y phoneNumber
        UpdatePatientDTO updateDTO = new UpdatePatientDTO(
                "Carlos",
                null,
                "555-9999",
                null,
                null,
                null
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // ACT
        UpdatedPatientDTO result = patientService.updatePatient(patientId, updateDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals("Carlos", result.firstName());
        assertEquals("555-9999", result.phoneNumber());
        // Verificar que los otros campos no cambiaron
        assertEquals("Perez", result.lastName());
        assertEquals("juan@mail.com", result.email());
        
        verify(patientRepository).findById(patientId);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe lanzar NoDataToUpdateException cuando no hay datos para actualizar")
    void updatePatientThrowsExceptionWhenNoData() {
        // ARRANGE
        Long patientId = 1L;
        User user = new User("juan@mail.com", "password", Role.ROLE_PATIENT);
        Patient patient = new Patient("Juan", "Perez", "555-1234",
                LocalDate.of(1990, 5, 15), user);

        // DTO sin datos (todos null)
        UpdatePatientDTO updateDTO = new UpdatePatientDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // ACT & ASSERT
        NoDataToUpdateException exception = assertThrows(
                NoDataToUpdateException.class,
                () -> patientService.updatePatient(patientId, updateDTO)
        );

        assertEquals("No hay datos por modificar", exception.getMessage());
        verify(patientRepository).findById(patientId);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException cuando el ID no existe al actualizar")
    void updatePatientThrowsExceptionWhenIdNotExists() {
        // ARRANGE
        Long nonExistentId = 999L;
        UpdatePatientDTO updateDTO = new UpdatePatientDTO(
                "Carlos",
                "Gomez",
                "555-9999",
                LocalDate.of(1995, 3, 10),
                "carlos@mail.com",
                "newPassword"
        );

        when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.updatePatient(nonExistentId, updateDTO)
        );

        assertEquals("El id ingresado no existe", exception.getMessage());
        verify(patientRepository).findById(nonExistentId);
    }
}
