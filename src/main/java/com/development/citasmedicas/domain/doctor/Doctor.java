package com.development.citasmedicas.domain.doctor;

import com.development.citasmedicas.domain.appointment.Appointment;
import com.development.citasmedicas.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "doctors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String cmp;

    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}
