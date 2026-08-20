````md
# Court Reserve

Court Reserve es una aplicación web para la **gestión de reservas de canchas deportivas**, desarrollada con Java, Spring Boot y PostgreSQL.

El sistema permite administrar usuarios, canchas y reservas aplicando reglas de negocio relacionadas con disponibilidad, horarios, duración, solapamiento de reservas y cálculo automático de precios.

El proyecto fue desarrollado en el contexto de la materia **Calidad del Software**, con especial énfasis en **pruebas unitarias, patrón AAA, pruebas de rendimiento, cobertura de código y automatización mediante Gradle**.

---

## Tecnologías utilizadas

### Backend
- Java 17
- Spring Boot 4.1.0
- Spring MVC
- Spring Data JPA
- Hibernate

### Base de datos
- PostgreSQL

### Frontend
- Thymeleaf
- HTML
- Bootstrap

### Testing y calidad
- JUnit 5
- Mockito
- JaCoCo
- Patrón AAA

### Construcción y automatización
- Gradle
- Gradle Wrapper

---

## Funcionalidades

### Usuarios

- Registrar usuarios.
- Listar usuarios registrados.
- Consultar usuarios por identificador.
- Validar correos electrónicos duplicados.

### Canchas

- Registrar canchas.
- Listar canchas.
- Consultar canchas por identificador.
- Activar y desactivar canchas.
- Definir tipo de cancha.
- Definir precio por hora.

### Reservas

- Crear reservas.
- Listar reservas.
- Seleccionar usuario y cancha.
- Validar que la cancha esté activa.
- Impedir reservas en fechas pasadas.
- Validar que la duración mínima sea de 1 hora.
- Validar que la duración máxima sea de 3 horas.
- Detectar solapamientos de horarios.
- Calcular automáticamente el precio total según la duración.
- Cancelar reservas.
- Mantener el historial de reservas canceladas.

---

## Arquitectura

El proyecto utiliza una arquitectura organizada por capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
````

La aplicación también utiliza DTOs para separar las solicitudes y respuestas de la representación interna de las entidades.

### Estructura principal

```text
com.courtreserve
├── controller
│   ├── UsuarioController
│   ├── CanchaController
│   ├── ReservaController
│   └── WebController
│
├── dto
│   ├── CrearUsuarioRequest
│   ├── UsuarioResponse
│   ├── CrearCanchaRequest
│   ├── CanchaResponse
│   ├── CrearReservaRequest
│   └── ReservaResponse
│
├── exception
│   └── GlobalExceptionHandler
│
├── model
│   ├── Usuario
│   ├── Cancha
│   ├── Reserva
│   ├── TipoCancha
│   └── EstadoReserva
│
├── repository
│   ├── UsuarioRepository
│   ├── CanchaRepository
│   └── ReservaRepository
│
└── service
    ├── UsuarioService
    ├── CanchaService
    └── ReservaService
```

---

## API REST

La aplicación expone endpoints REST para gestionar usuarios, canchas y reservas.

### Usuarios

| Método | Endpoint             | Descripción                 |
| ------ | -------------------- | --------------------------- |
| `POST` | `/api/usuarios`      | Crear un usuario            |
| `GET`  | `/api/usuarios`      | Listar usuarios             |
| `GET`  | `/api/usuarios/{id}` | Consultar un usuario por ID |

### Canchas

| Método  | Endpoint                               | Descripción                 |                                 |
| ------- | -------------------------------------- | --------------------------- | ------------------------------- |
| `POST`  | `/api/canchas`                         | Crear una cancha            |                                 |
| `GET`   | `/api/canchas`                         | Listar canchas              |                                 |
| `GET`   | `/api/canchas/{id}`                    | Consultar una cancha por ID |                                 |
| `PATCH` | `/api/canchas/{id}/estado?activa={true | false}`                     | Activar o desactivar una cancha |

### Reservas

| Método  | Endpoint                      | Descripción          |
| ------- | ----------------------------- | -------------------- |
| `POST`  | `/api/reservas`               | Crear una reserva    |
| `GET`   | `/api/reservas`               | Listar reservas      |
| `PATCH` | `/api/reservas/{id}/cancelar` | Cancelar una reserva |

---

## Pruebas unitarias

Las pruebas unitarias se encuentran organizadas por servicio y utilizan **JUnit 5 + Mockito**.

```text
src/test/java/com/courtreserve
├── service
│   ├── UsuarioServiceTest.java
│   ├── CanchaServiceTest.java
│   └── ReservaServiceTest.java
│
└── performance
    └── ReservaServicePerformanceTest.java
```

Las pruebas siguen el patrón **AAA**:

```text
ARRANGE → Preparación de datos, mocks y comportamiento esperado
ACT     → Ejecución del método bajo prueba
ASSERT  → Validación del resultado y de las interacciones
```

### Casos de prueba destacados

#### UsuarioService

Se verifica, entre otros escenarios:

* Creación correcta de un usuario.
* Rechazo de usuarios con correo electrónico duplicado.
* Verificación de interacciones con el repositorio mediante Mockito.

#### CanchaService

Se prueban escenarios como:

* Creación de canchas.
* Listado de canchas.
* Búsqueda por identificador.
* Manejo de canchas inexistentes.
* Activación de canchas.
* Desactivación de canchas.

#### ReservaService

Las pruebas validan las principales reglas de negocio del sistema:

* Rechazo de reservas en fechas pasadas.
* Rechazo de reservas con duración superior a 3 horas.
* Rechazo de reservas con duración inferior a 1 hora.
* Validación de que la hora final sea posterior a la inicial.
* Aceptación de reservas de exactamente 1 hora.
* Rechazo de reservas sobre canchas inactivas.
* Detección de solapamientos.
* Creación correcta de una reserva válida.
* Cálculo automático del precio.
* Cálculo de precios para duraciones fraccionadas, como 1 hora y 30 minutos.

---

## Pruebas de rendimiento

El proyecto incluye pruebas unitarias de rendimiento mediante `assertTimeout` de JUnit.

Actualmente se evalúan los siguientes escenarios:

| Operación                         |        Criterio |
| --------------------------------- | --------------: |
| Crear una reserva                 | Menos de 100 ms |
| Calcular el precio de una reserva |  Menos de 20 ms |
| Listar 10.000 reservas            | Menos de 100 ms |

Estas pruebas se ejecutan con dependencias simuladas mediante Mockito, por lo que están orientadas a evaluar el comportamiento del servicio de manera aislada y no el rendimiento completo de PostgreSQL o de la aplicación desplegada.

---

## Cobertura de código

El proyecto utiliza **JaCoCo** para generar reportes de cobertura de las pruebas.

La configuración genera:

* Reporte HTML.
* Reporte XML.

Para ejecutar las pruebas y generar el reporte:

```bash
./gradlew test jacocoTestReport
```

En Windows:

```powershell
.\gradlew.bat test jacocoTestReport
```

El reporte HTML generado puede consultarse en:

```text
build/reports/jacoco/test/html/index.html
```

---

## Ejecución del proyecto

### Requisitos

Antes de ejecutar Court Reserve se requiere:

* Java 17.
* PostgreSQL.
* Git.

No es necesario instalar Gradle de forma global, ya que el proyecto incluye **Gradle Wrapper**.

### 1. Clonar el repositorio

```bash
git clone https://github.com/AndresVillotaVillota/court-reserve.git
cd court-reserve
```

### 2. Configurar PostgreSQL

Configura la conexión a PostgreSQL en:

```text
src/main/resources/application.properties
```

La base de datos debe estar disponible antes de iniciar la aplicación.

### 3. Compilar y ejecutar las pruebas

En Windows:

```powershell
.\gradlew.bat clean build
```

En Linux/macOS:

```bash
./gradlew clean build
```

### 4. Ejecutar la aplicación

En Windows:

```powershell
.\gradlew.bat bootRun
```

En Linux/macOS:

```bash
./gradlew bootRun
```

Una vez iniciada, la aplicación se encuentra disponible de forma predeterminada en:

```text
http://localhost:8080
```

---

## Objetivos de calidad aplicados

Durante el desarrollo se trabajaron conceptos relacionados con:

* Separación de responsabilidades.
* Arquitectura por capas.
* DTOs para solicitudes y respuestas.
* Validación de reglas de negocio.
* Manejo centralizado de excepciones.
* Pruebas unitarias aisladas.
* Mocking de dependencias.
* Patrón AAA.
* Pruebas de rendimiento.
* Cobertura mediante JaCoCo.
* Automatización de construcción y pruebas con Gradle.

---

## Autor

**Andrés Alejandro Villota Villota**

Estudiante de Ingeniería de Sistemas — Universidad de Antioquia

* GitHub: [AndresVillotaVillota](https://github.com/AndresVillotaVillota)
* Correo: [andres.villotav@udea.edu.co](mailto:andres.villotav@udea.edu.co)

```
```
