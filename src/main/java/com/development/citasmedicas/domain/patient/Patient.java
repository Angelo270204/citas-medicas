package com.development.citasmedicas.domain.patient;

import com.development.citasmedicas.domain.appointment.Appointment;
import com.development.citasmedicas.domain.patient.dto.UpdatePatientDTO;
import com.development.citasmedicas.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    public Patient(String firstName, String lastName,String phoneNumber, LocalDate birthDate, User user){
        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.birthDate=birthDate;
        this.user=user;
    }

    public void updatePatient(UpdatePatientDTO dto){
        if(dto.firstName() != null){
            this.firstName = dto.firstName();
        }

        if(dto.lastName() != null){
            this.lastName = dto.lastName();
        }

        if(dto.phoneNumber() != null){
            this.phoneNumber=dto.phoneNumber();
        }

        if(dto.birthDate()!=null){
            this.birthDate = dto.birthDate();
        }

        if(this.user != null){
            user.updateUser(dto.email(), dto.password());
        }
    }
}
