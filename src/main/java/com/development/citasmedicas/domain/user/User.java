package com.development.citasmedicas.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enable = true;

    public User(String email, String password){
        this.email=email;
        this.password=password;
        this.role=Role.ROLE_PATIENT;
    }

    public void updateUser(String email, String password){
        if(email != null){
            this.email = email;
        }

        if(password != null){
            this. password = password;
        }
    }
}
