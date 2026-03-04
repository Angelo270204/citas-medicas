package com.development.citasmedicas.domain.patient;

import java.util.Optional;

import com.development.citasmedicas.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserEmail(String email);
    Optional<Patient> findByUser(User user);
}