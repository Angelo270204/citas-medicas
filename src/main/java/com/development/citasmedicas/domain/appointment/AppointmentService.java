package com.development.citasmedicas.domain.appointment;

import com.development.citasmedicas.domain.appointment.dto.AppointmentAdminResponseDTO;
import com.development.citasmedicas.domain.appointment.dto.AppointmentResponseDTO;
import com.development.citasmedicas.domain.appointment.dto.ScheduleAppointmentDTO;
import com.development.citasmedicas.domain.appointment.exception.AppointmentConflictException;
import com.development.citasmedicas.domain.appointment.exception.InvalidAppointmentDurationException;
import com.development.citasmedicas.domain.appointment.exception.InvalidAppointmentStatusException;
import com.development.citasmedicas.domain.appointment.exception.InvalidAppointmentTimeRangeException;
import com.development.citasmedicas.domain.appointment.exception.InvalidBusinessHoursException;
import com.development.citasmedicas.domain.doctor.DoctorRepository;
import com.development.citasmedicas.domain.patient.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private static final int MIN_APPOINTMENT_MINUTES = 30;
    private static final int MAX_APPOINTMENT_HOURS = 1;
    private static final int BUSINESS_START_HOUR = 8;
    private static final int BUSINESS_END_HOUR = 18;

    public AppointmentService(AppointmentRepository repository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.repository = repository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public List<AppointmentAdminResponseDTO> getAllAppointmentsAdmin() {
        return repository.findAll()
                .stream()
                .map(AppointmentAdminResponseDTO::new)
                .toList();
    }

    public List<AppointmentResponseDTO> getAllAppointments() {
        return repository.findAll()
                .stream()
                .map(AppointmentResponseDTO::new)
                .toList();
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        var appointment = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        return new AppointmentResponseDTO(appointment);
    }

    @Transactional
    public AppointmentResponseDTO scheduledAppointment(ScheduleAppointmentDTO app) {
        validateAppointmentTimeRange(app.startDateTime(), app.endDateTime());
        validateAppointmentDuration(app.startDateTime(), app.endDateTime());
        validateBusinessHours(app.startDateTime(), app.endDateTime());

        if (repository.existsConflictingAppointment(app.doctorId(), app.startDateTime(), app.endDateTime())) {
            throw new AppointmentConflictException("El doctor ya tiene una cita agendada en ese horario");
        }

        if (repository.existsPatientConflictingAppointment(app.patientId(), app.startDateTime(), app.endDateTime())) {
            throw new AppointmentConflictException("El paciente ya tiene una cita agendada en ese horario");
        }

        var doctor = doctorRepository.findById(app.doctorId()).orElseThrow(() -> new EntityNotFoundException("El doctor que eligio no existe"));
        var patient = patientRepository.findById(app.patientId()).orElseThrow(() -> new EntityNotFoundException("El paciente elegido no existe"));

        var appointment = new Appointment(app, doctor, patient);

        return new AppointmentResponseDTO(repository.save(appointment));
    }

    @Transactional
    public void cancelAppointment(Long id) {
        var appointment = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("El id ingresado no existe"));

        if (appointment.getStatus() == AppointmentStatus.CANCELED) {
            throw new InvalidAppointmentStatusException("La cita ya está cancelada");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentStatusException("No se puede cancelar una cita que ya fue completada");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
    }

    private void validateAppointmentTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new InvalidAppointmentTimeRangeException("La fecha de inicio debe ser antes de la fecha de fin");
        }
    }

    private void validateAppointmentDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long minutes = duration.toMinutes();

        if (minutes < MIN_APPOINTMENT_MINUTES) {
            throw new InvalidAppointmentDurationException("La duración mínima de una cita es de " + MIN_APPOINTMENT_MINUTES + " minutos");
        }

        if (duration.toHours() > MAX_APPOINTMENT_HOURS) {
            throw new InvalidAppointmentDurationException("La duración máxima de una cita es de " + MAX_APPOINTMENT_HOURS + " horas");
        }
    }

    private void validateBusinessHours(LocalDateTime start, LocalDateTime end) {
        DayOfWeek dayOfWeek = start.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new InvalidBusinessHoursException("No se pueden agendar citas los fines de semana");
        }

        int startHour = start.getHour();
        int endHour = end.getHour();

        if (startHour < BUSINESS_START_HOUR || endHour > BUSINESS_END_HOUR || (endHour == BUSINESS_END_HOUR && end.getMinute() > 0)) {
            throw new InvalidBusinessHoursException("Las citas solo pueden agendarse entre las " + BUSINESS_START_HOUR + ":00 y las " + BUSINESS_END_HOUR + ":00");
        }
    }
}
