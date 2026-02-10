package com.development.citasmedicas.domain.doctor;

import com.development.citasmedicas.domain.appointment.Appointment;
import com.development.citasmedicas.domain.doctor.dto.UpdateDoctorDTO;
import com.development.citasmedicas.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "doctors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    public Doctor(String firstName, String lastName, String cmp, Specialty specialty, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cmp = cmp;
        this.specialty = specialty;
        this.user = user;
    }

    public void updateDoctor(UpdateDoctorDTO dto){
        if(dto.firstName()!=null){
            this.firstName=dto.firstName();
        }

        if(dto.lastName()!=null){
            this.lastName=dto.lastName();
        }

        if(dto.cmp()!=null){
            this.cmp=dto.cmp();
        }

        if(dto.specialty()!=null){
            this.specialty=dto.specialty();
        }

        if(this.user != null){
            user.updateUser(dto.email(),dto.password());
        }
    }
}
