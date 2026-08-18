# CourtReserve

CourtReserve es una aplicación web para la gestión de reservas de canchas deportivas.

El sistema permite registrar usuarios, administrar canchas y gestionar reservas, aplicando reglas de negocio relacionadas con disponibilidad, horarios, duración y solapamiento de reservas.

Este proyecto fue desarrollado como parte de la materia **Calidad del Software**, con énfasis en pruebas unitarias, patrón AAA, pruebas de rendimiento, cobertura de código y automatización mediante Gradle.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 4.1.0
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Thymeleaf
- Bootstrap
- Gradle
- JUnit 5
- Mockito
- JaCoCo

---

## Funcionalidades

### Usuarios

- Registrar usuarios.
- Listar usuarios registrados.
- Validar correos duplicados.

### Canchas

- Registrar canchas.
- Listar canchas.
- Activar y desactivar canchas.
- Definir tipo y precio por hora.

### Reservas

- Crear reservas.
- Seleccionar usuario y cancha.
- Validar que la cancha esté activa.
- Impedir reservas en fechas pasadas.
- Validar duración mínima de 1 hora.
- Validar duración máxima de 3 horas.
- Detectar solapamientos de horarios.
- Calcular automáticamente el precio total.
- Cancelar reservas.
- Mantener historial de reservas canceladas.

---

## Arquitectura

El proyecto utiliza una arquitectura por capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL