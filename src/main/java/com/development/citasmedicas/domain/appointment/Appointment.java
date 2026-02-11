package com.development.citasmedicas.domain.appointment;

import com.development.citasmedicas.domain.appointment.dto.ScheduleAppointmentDTO;
import com.development.citasmedicas.domain.doctor.Doctor;
import com.development.citasmedicas.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="appointments")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "reason_for_visit")
    private String reasonForVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public Appointment(ScheduleAppointmentDTO dto, Doctor doctor, Patient patient){
        this.startDateTime= dto.startDateTime();
        this.endDateTime=dto.endDateTime();
        this.reasonForVisit= dto.reasonForVisit();
        this.status=AppointmentStatus.PENDING;
        this.doctor=doctor;
        this.patient=patient;
    }
}
