package com.development.citasmedicas.medicos;

import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.doctor.DoctorRepository;
import com.development.citasmedicas.domain.doctor.DoctorService;
import com.development.citasmedicas.domain.doctor.Specialty;
import com.development.citasmedicas.domain.doctor.dto.CreateDoctorDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorAdminResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.DoctorResponseDTO;
import com.development.citasmedicas.domain.doctor.dto.UpdateDoctorDTO;
import com.development.citasmedicas.domain.exception.NoDataToUpdateException;
import com.development.citasmedicas.domain.user.Role;
import com.development.citasmedicas.domain.user.User;
import com.development.citasmedicas.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    @DisplayName("Retornar solo doctores habilitados cuando se llame getAllDoctors")
    void getAllDoctorsShouldReturnOnlyEnabledDoctors() {
        //ARRANGE
        User user1 = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        User user2 = new User("carlos@gmail.com", "password456", Role.ROLE_DOCTOR);
        User user3 = new User("maria@gmail.com", "password789", Role.ROLE_DOCTOR);

        user2.setEnable(false); // ← Deshabilitado
        user3.setEnable(false); // ← Deshabilitado
        Doctor doctor1 = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user1);
        Doctor doctor2 = new Doctor("Carlos", "Ramirez", "CMP-456", Specialty.CARDIOLOGY, user2);
        Doctor doctor3 = new Doctor("Maria", "Lopez", "CMP-789", Specialty.PEDIATRICS, user3);
        List<Doctor> doctors = List.of(doctor1, doctor2, doctor3);
        when(doctorRepository.findAll()).thenReturn(doctors);
        //ACT
        List<DoctorResponseDTO> result = doctorService.getAllDoctors();
        //ASSERT
        assertNotNull(result);
        assertEquals(1, result.size()); // ← Solo retorna 1 (el habilitado)
        assertEquals("Angelo", result.getFirst().firstName());
        assertEquals("angelo@gmail.com", result.getFirst().email());
    }

    @Test
    @DisplayName("Retornar todos los doctores (habilitados y deshabilitados) cuando se llame getAllDoctorsForAdmin")
    void getAllDoctorsWhenUserRoleIsAdmin() {
        //ARRANGE
        User user1 = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        User user2 = new User("carlos@gmail.com", "password456", Role.ROLE_DOCTOR);
        user2.setEnable(false); // ← Usuario deshabilitado
        Doctor doctor1 = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user1);
        Doctor doctor2 = new Doctor("Carlos", "Ramirez", "CMP-456", Specialty.CARDIOLOGY, user2);
        List<Doctor> doctors = List.of(doctor1, doctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);
        //ACT
        List<DoctorAdminResponseDTO> result = doctorService.getAllDoctorsForAdmin();
        //ASSERT
        assertNotNull(result);
        assertEquals(2, result.size()); // ← Retorna AMBOS (habilitado + deshabilitado)

        // Verificar que incluye el campo 'enable'
        assertTrue(result.get(0).enable());  // Angelo está habilitado
        assertFalse(result.get(1).enable()); // Carlos está deshabilitado
    }

    @Test
    @DisplayName("Retornar un doctor por ID cuando existe")
    void getDoctorByIdShouldReturnDoctorWhenExists() {
        //ARRANGE
        Long doctorId = 1L;
        User user = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        //ACT
        DoctorResponseDTO result = doctorService.getDoctorById(doctorId);
        //ASSERT
        assertNotNull(result);
        assertEquals(doctorId, result.id());
        assertEquals("Angelo", result.firstName());
        assertEquals("Rubinos", result.lastName());
        assertEquals("angelo@gmail.com", result.email());
    }

    @Test
    @DisplayName("Lanzar EntityNotFoundException cuando el doctor no existe")
    void getDoctorByIdShouldThrowExceptionWhenNotExists() {
        //ARRANGE
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.getDoctorById(doctorId)
        );
        assertEquals("El id ingresado no existe", exception.getMessage());
    }

    @Test
    @DisplayName("Retornar un doctor con campo enable para admin cuando existe")
    void getDoctorByIdForAdminShouldReturnDoctorWithEnableField() {
        //ARRANGE
        Long doctorId = 1L;
        User user = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        user.setEnable(false); // ← Deshabilitado

        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        //ACT
        DoctorAdminResponseDTO result = doctorService.getDoctorByIdForAdmin(doctorId);
        //ASSERT
        assertNotNull(result);
        assertEquals(doctorId, result.id());
        assertFalse(result.enable()); // ← Verifica que incluye el campo enable
    }

    @Test
    @DisplayName("Lanzar EntityNotFoundException para admin cuando el doctor no existe")
    void getDoctorByIdForAdminShouldThrowExceptionWhenNotExists() {
        //ARRANGE
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.getDoctorByIdForAdmin(doctorId)
        );
        assertEquals("El id ingresado no existe", exception.getMessage());
    }

    @Test
    @DisplayName("Crear un doctor exitosamente cuando los datos son válidos")
    void createDoctorShouldReturnCreatedDoctorWhenDataIsValid() {
        //ARRANGE
        CreateDoctorDTO dto = new CreateDoctorDTO(
                "Angelo",
                "Rubinos",
                "CMP-123",
                Specialty.GENERAL_MEDICINE,
                "angelo@gmail.com",
                "password123"
        );
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword123");

        // Mockear el comportamiento de save
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor savedDoctor = invocation.getArgument(0);
            savedDoctor.setId(1L);
            return savedDoctor;
        });
        //ACT
        DoctorResponseDTO result = doctorService.createDoctor(dto);
        //ASSERT
        assertNotNull(result);
        assertEquals("Angelo", result.firstName());
        assertEquals("Rubinos", result.lastName());
        assertEquals("CMP-123", result.cmp());
        assertEquals(Specialty.GENERAL_MEDICINE, result.specialty());
        assertEquals("angelo@gmail.com", result.email());

        // Verificar que se llamó a los métodos correctos
        verify(userRepository).existsByEmail(dto.email());
        verify(passwordEncoder).encode(dto.password());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Lanzar IllegalArgumentException cuando el email ya existe")
    void createDoctorShouldThrowExceptionWhenEmailAlreadyExists() {
        //ARRANGE
        CreateDoctorDTO dto = new CreateDoctorDTO(
                "Angelo",
                "Rubinos",
                "CMP-123",
                Specialty.GENERAL_MEDICINE,
                "angelo@gmail.com",
                "password123"
        );
        when(userRepository.existsByEmail(dto.email())).thenReturn(true); // ← Email ya existe
        //ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.createDoctor(dto)
        );
        assertEquals("El email ingresado ya existe", exception.getMessage());

        // Verificar que NO se intentó guardar
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Deshabilitar un doctor cuando existe (eliminación lógica)")
    void deleteDoctorShouldDisableUserWhenDoctorExists() {
        //ARRANGE
        Long doctorId = 1L;
        User user = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        user.setEnable(true); // ← Inicialmente habilitado

        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        //ACT
        doctorService.deleteDoctor(doctorId);
        //ASSERT
        assertFalse(doctor.getUser().isEnable()); // ← Verifica que fue deshabilitado
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    @DisplayName("Lanzar EntityNotFoundException cuando se intenta eliminar un doctor inexistente")
    void deleteDoctorShouldThrowExceptionWhenDoctorNotExists() {
        //ARRANGE
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.deleteDoctor(doctorId)
        );
        assertEquals("El id ingresado no existe", exception.getMessage());
    }

    @Test
    @DisplayName("Actualizar un doctor exitosamente cuando hay datos válidos")
    void updateDoctorShouldUpdateFieldsSuccessfully() {
        //ARRANGE
        Long doctorId = 1L;
        User user = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);
        UpdateDoctorDTO dto = new UpdateDoctorDTO(
                "Carlos",           // ← Cambio de nombre
                "Ramirez",          // ← Cambio de apellido
                null,               // No cambia CMP
                Specialty.CARDIOLOGY, // ← Cambio de especialidad
                "carlos@gmail.com", // ← Cambio de email
                "newPassword456"    // ← Cambio de password
        );
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedNewPassword456");
        //ACT
        DoctorResponseDTO result = doctorService.updateDoctor(doctorId, dto);
        //ASSERT
        assertNotNull(result);
        assertEquals("Carlos", result.firstName());
        assertEquals("Ramirez", result.lastName());
        assertEquals("CMP-123", result.cmp()); // ← No cambió
        assertEquals(Specialty.CARDIOLOGY, result.specialty());
        assertEquals("carlos@gmail.com", result.email());

        verify(passwordEncoder).encode(dto.password());
    }

    @Test
    @DisplayName("Lanzar NoDataToUpdateException cuando no hay datos para actualizar")
    void updateDoctorShouldThrowExceptionWhenNoDataToUpdate() {
        //ARRANGE
        Long doctorId = 1L;

        // Crear doctor que SÍ existe en BD
        User user = new User("angelo@gmail.com", "password123", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);

        // Mockear que el doctor existe
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // DTO vacío (sin datos para actualizar)
        UpdateDoctorDTO dto = new UpdateDoctorDTO(null, null, null, null, null, null);
        //ACT & ASSERT
        NoDataToUpdateException exception = assertThrows(
                NoDataToUpdateException.class,
                () -> doctorService.updateDoctor(doctorId, dto)
        );
        assertEquals("No hay ningun dato por modificar", exception.getMessage());

        // Verificar que SÍ se buscó el doctor (es parte del flujo esperado)
        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    @DisplayName("Lanzar EntityNotFoundException cuando el doctor a actualizar no existe")
    void updateDoctorShouldThrowExceptionWhenDoctorNotExists() {
        //ARRANGE
        Long doctorId = 999L;
        UpdateDoctorDTO dto = new UpdateDoctorDTO(
                "Carlos", null, null, null, null, null
        );
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.updateDoctor(doctorId, dto)
        );
        assertEquals("El id ingresado no existe", exception.getMessage());
    }

    @Test
    @DisplayName("Encriptar la contraseña cuando se proporciona en la actualización")
    void updateDoctorShouldEncryptPasswordWhenProvided() {
        //ARRANGE
        Long doctorId = 1L;
        User user = new User("angelo@gmail.com", "oldEncodedPassword", Role.ROLE_DOCTOR);
        Doctor doctor = new Doctor("Angelo", "Rubinos", "CMP-123", Specialty.GENERAL_MEDICINE, user);
        doctor.setId(doctorId);
        UpdateDoctorDTO dto = new UpdateDoctorDTO(
                null, null, null, null, null, "newPassword123"
        );
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(passwordEncoder.encode(dto.password())).thenReturn("newEncodedPassword123");
        //ACT
        doctorService.updateDoctor(doctorId, dto);
        //ASSERT
        assertEquals("newEncodedPassword123", doctor.getUser().getPassword());
        verify(passwordEncoder).encode("newPassword123");
    }
}
