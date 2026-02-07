-- 1. Tabla de Usuarios
CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL,
    enable   TINYINT(1)   NOT NULL DEFAULT 1 -- En MySQL Boolean es TINYINT
);

-- 2. Tabla de Doctores
CREATE TABLE doctors
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    cmp        VARCHAR(20)  NOT NULL UNIQUE,
    specialty  VARCHAR(50)  NOT NULL,
    user_id    BIGINT       NOT NULL UNIQUE,
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 3. Tabla de Pacientes
CREATE TABLE patients
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    birth_date   DATE         NOT NULL,
    user_id      BIGINT       NOT NULL UNIQUE,
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 4. Tabla de Citas
CREATE TABLE appointments
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date_time  DATETIME    NOT NULL, -- En MySQL usamos DATETIME
    end_date_time    DATETIME    NOT NULL,
    reason_for_visit VARCHAR(255),
    diagnosis        TEXT,
    status           VARCHAR(50) NOT NULL,
    doctor_id        BIGINT      NOT NULL,
    patient_id       BIGINT      NOT NULL,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients (id)
);

-- Índices (MySQL los necesita igual)
CREATE INDEX idx_appointment_doctor ON appointments (doctor_id);
CREATE INDEX idx_appointment_date ON appointments (start_date_time);
