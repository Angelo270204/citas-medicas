# 🏥 Citas Médicas API

Sistema de gestión de citas médicas desarrollado con **Spring Boot 4.0** y **Java 21**. API REST completa para administrar doctores, pacientes y citas médicas con autenticación JWT.

## 📋 Características

- **Autenticación JWT**: Sistema de login seguro con tokens JWT e información del usuario
- **Gestión de Doctores**: CRUD completo con especialidades médicas
- **Gestión de Pacientes**: Registro y administración de pacientes
- **Sistema de Citas**: Agendamiento con validaciones de horarios y conflictos
- **Roles y Permisos**: Control de acceso basado en roles (ADMIN, DOCTOR, PATIENT)
- **CORS Configurado**: Soporte para frontend en localhost:4200
- **Migraciones de BD**: Flyway para versionamiento de esquema
- **Docker Ready**: Configuración completa para despliegue con Docker

## 🛠️ Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.0.2 |
| Spring Security | Incluido |
| Spring Data JPA | Incluido |
| MySQL | 8.0 |
| Flyway | Incluido |
| JWT (jjwt) | 0.13.0 |
| Lombok | Incluido |
| Docker | 3.8+ |

## 🏗️ Arquitectura

```
src/main/java/com/development/citasmedicas/
├── controller/           # Controladores REST
│   ├── AuthController    # Autenticación (login/registro)
│   ├── DoctorController  # CRUD de doctores
│   ├── PatientController # CRUD de pacientes
│   └── AppointmentController # Gestión de citas
├── domain/
│   ├── appointment/      # Entidad y lógica de citas
│   ├── doctor/          # Entidad y lógica de doctores
│   ├── patient/         # Entidad y lógica de pacientes
│   ├── user/            # Entidad de usuarios y autenticación
│   └── exception/       # Excepciones de dominio
└── infra/
    ├── security/        # Configuración JWT y Spring Security
    └── exception/       # Manejador global de excepciones
```

## 🚀 Instalación y Ejecución

### Prerequisitos

- Java 21
- Maven 3.9+
- Docker y Docker Compose (opcional)

### Variables de Entorno

Crea un archivo `.env` basándote en `.env.example`:

```bash
cp .env.example .env
```

Configura las siguientes variables:

```env
DB_ROOT_PASSWORD=tu_password_root
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
SECRET=tu_secret_key_jwt_largo_y_seguro
```

### Con Docker (Recomendado)

```bash
# Construir y ejecutar
docker-compose up --build

# Solo ejecutar (si ya está construido)
docker-compose up -d
```

La aplicación estará disponible en `http://localhost:8080`

### Sin Docker

```bash
# Compilar
./mvnw clean package -DskipTests

# Ejecutar (requiere MySQL configurado)
java -jar target/citas-medicas-0.0.1-SNAPSHOT.jar
```

## 📡 API Endpoints

### Autenticación

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| POST | `/api/auth/login` | Iniciar sesión | Público |
| POST | `/api/auth/register` | Registrar paciente | Público |

### Doctores

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/doctors` | Listar doctores (público) | Público |
| GET | `/api/doctors/admin` | Listar doctores con info completa | ADMIN |
| GET | `/api/doctors/{id}` | Obtener doctor por ID | Público |
| GET | `/api/doctors/admin/{id}` | Obtener doctor con info completa | ADMIN |
| POST | `/api/doctors` | Crear doctor | ADMIN |
| PUT | `/api/doctors/{id}` | Actualizar doctor | ADMIN |
| DELETE | `/api/doctors/{id}` | Eliminar doctor | ADMIN |

### Pacientes

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/patients` | Listar pacientes | ADMIN |
| GET | `/api/patients/{id}` | Obtener paciente | ADMIN |
| PUT | `/api/patients/{id}` | Actualizar paciente | ADMIN |
| DELETE | `/api/patients/{id}` | Eliminar paciente | ADMIN |

### Citas

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| GET | `/api/appointments` | Mis citas (paciente o doctor) | PATIENT/DOCTOR |
| GET | `/api/appointments/all` | Listar todas las citas | ADMIN |
| GET | `/api/appointments/{id}` | Obtener cita por ID | Autenticado |
| POST | `/api/appointments` | Agendar cita (paciente) | PATIENT |
| POST | `/api/appointments/admin` | Agendar cita (cualquier paciente) | ADMIN/DOCTOR |
| DELETE | `/api/appointments/{id}` | Cancelar cita | Autenticado |
| PATCH | `/api/appointments/{id}/complete` | Completar cita con diagnóstico | DOCTOR |

## 📝 Ejemplos de Uso

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin@example.com", "password": "password123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "admin@example.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "ROLE_ADMIN"
  }
}
```

### Registrar Paciente

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "password": "password123",
    "phoneNumber": "999888777",
    "birthDate": "1990-05-15"
  }'
```

### Agendar Cita (como Paciente)

```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "doctorId": 1,
    "startDateTime": "2026-03-10T09:00:00",
    "endDateTime": "2026-03-10T09:30:00",
    "reasonForVisit": "Consulta general"
  }'
```

### Agendar Cita (como Admin/Doctor)

```bash
curl -X POST http://localhost:8080/api/appointments/admin \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "doctorId": 1,
    "patientId": 2,
    "startDateTime": "2026-03-10T09:00:00",
    "endDateTime": "2026-03-10T09:30:00",
    "reasonForVisit": "Consulta general"
  }'
```

### Obtener Mis Citas

```bash
# Como paciente: devuelve las citas del paciente autenticado
# Como doctor: devuelve las citas del doctor autenticado
curl -X GET http://localhost:8080/api/appointments \
  -H "Authorization: Bearer {token}"
```

## ⚕️ Especialidades Médicas

- `GENERAL_MEDICINE` - Medicina General
- `PEDIATRICS` - Pediatría
- `CARDIOLOGY` - Cardiología
- `DERMATOLOGY` - Dermatología
- `GYNECOLOGY` - Ginecología

## 📅 Reglas de Negocio para Citas

- **Horario laboral**: Lunes a Viernes, 8:00 AM - 6:00 PM
- **Duración mínima**: 30 minutos
- **Duración máxima**: 1 hora
- **Sin conflictos**: Un doctor/paciente no puede tener citas solapadas

## 🔐 Roles

| Rol | Descripción |
|-----|-------------|
| `ROLE_ADMIN` | Acceso total al sistema |
| `ROLE_DOCTOR` | Gestión de citas y diagnósticos |
| `ROLE_PATIENT` | Usuario paciente básico |

## 📁 Estructura de la Base de Datos

```sql
users          # Usuarios del sistema (email, password, rol)
doctors        # Doctores (nombre, CMP, especialidad)
patients       # Pacientes (nombre, teléfono, fecha nacimiento)
appointments   # Citas (fecha, estado, diagnóstico)
```

## 🐳 Docker

### Servicios

- **mysql**: Base de datos MySQL 8.0
- **app**: Aplicación Spring Boot

### Volúmenes

- `mysql_data`: Persistencia de datos de MySQL

## 📄 Licencia

Este proyecto está bajo desarrollo.

## 👨‍💻 Autor

Desarrollado como proyecto de gestión de citas médicas con Spring Boot.
