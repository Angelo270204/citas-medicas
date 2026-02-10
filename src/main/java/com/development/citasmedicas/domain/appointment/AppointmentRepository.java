package com.development.citasmedicas.domain.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.status != 'CANCELED'
        AND (a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime)
    """)
    boolean existsConflictingAppointment(
        @Param("doctorId") Long doctorId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.patient.id = :patientId
        AND a.status != 'CANCELED'
        AND (a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime)
    """)
    boolean existsPatientConflictingAppointment(
        @Param("patientId") Long patientId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    // Para ver el historial de un paciente
    List<Appointment> findByPatientId(Long patientId);

    // Para ver la agenda del médico en un rango (ej: citas de hoy)
    List<Appointment> findByDoctorIdAndStartDateTimeBetween(
        Long doctorId, LocalDateTime start, LocalDateTime end
    );
}