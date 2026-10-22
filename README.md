# Court Reserve

**Sistema web para la gestión de reservas de canchas deportivas.**

Court Reserve es una aplicación web desarrollada con **Java, Spring Boot y PostgreSQL** que permite administrar usuarios, canchas y reservas.

El sistema implementa reglas de negocio para controlar la disponibilidad de las canchas, validar horarios y duración de las reservas, evitar solapamientos y calcular automáticamente el precio según el tiempo reservado.

El proyecto fue desarrollado en el contexto de la materia **Calidad del Software**, con especial énfasis en **pruebas unitarias, patrón AAA, pruebas de rendimiento, cobertura de código y automatización mediante Gradle**.

---

## Tecnologías utilizadas

| Área | Tecnologías |
| --- | --- |
| **Backend** | Java 17, Spring Boot 4.1.0, Spring MVC |
| **Persistencia** | Spring Data JPA, Hibernate |
| **Base de datos** | PostgreSQL |
| **Frontend** | Thymeleaf, HTML, Bootstrap 5.3.8 (CDN con verificación de integridad) |
| **Testing** | JUnit 5, Mockito |
| **Calidad** | JaCoCo, patrón AAA, pruebas de rendimiento |
| **Construcción** | Gradle, Gradle Wrapper |

---

## Funcionalidades principales

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
- Definir el tipo de cancha.
- Definir el precio por hora.

### Reservas

- Crear reservas.
- Listar reservas.
- Seleccionar usuario y cancha.
- Validar que la cancha se encuentre activa.
- Impedir reservas en fechas pasadas.
- Validar una duración mínima de 1 hora.
- Validar una duración máxima de 3 horas.
- Detectar solapamientos de horarios.
- Calcular automáticamente el precio total según la duración.
- Cancelar reservas.
- Mantener el historial de reservas canceladas.

---

## Arquitectura

Court Reserve utiliza una **arquitectura por capas**, separando las responsabilidades relacionadas con la exposición de la API, la lógica de negocio y el acceso a los datos.

```text
┌─────────────────────┐
│     Controller      │
│      REST / MVC     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│       Service       │
│  Lógica de negocio  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     Repository      │
│   Spring Data JPA   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│     PostgreSQL      │
└─────────────────────┘
```

La aplicación también utiliza **DTOs (Data Transfer Objects)** para separar las solicitudes y respuestas de la API de la representación interna de las entidades.

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

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/usuarios` | Crear un usuario |
| `GET` | `/api/usuarios` | Listar usuarios |
| `GET` | `/api/usuarios/{id}` | Consultar un usuario por ID |

### Canchas

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/canchas` | Crear una cancha |
| `GET` | `/api/canchas` | Listar canchas |
| `GET` | `/api/canchas/{id}` | Consultar una cancha por ID |
| `PATCH` | `/api/canchas/{id}/estado?activa=true` | Activar o desactivar una cancha |

El parámetro `activa` acepta los valores `true` o `false`.

### Reservas

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/reservas` | Crear una reserva |
| `GET` | `/api/reservas` | Listar reservas |
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

Las pruebas siguen el patrón **AAA (Arrange, Act, Assert)**:

```text
ARRANGE → Preparación de datos, mocks y comportamiento esperado
ACT     → Ejecución del método bajo prueba
ASSERT  → Validación del resultado y de las interacciones
```

### Casos de prueba destacados

#### UsuarioService

Se verifican escenarios como:

- Creación correcta de un usuario.
- Rechazo de usuarios con correo electrónico duplicado.
- Verificación de interacciones con el repositorio mediante Mockito.

#### CanchaService

Se prueban escenarios como:

- Creación de canchas.
- Listado de canchas.
- Búsqueda por identificador.
- Manejo de canchas inexistentes.
- Activación de canchas.
- Desactivación de canchas.

#### ReservaService

Las pruebas validan las principales reglas de negocio del sistema:

- Rechazo de reservas en fechas pasadas.
- Rechazo de reservas con duración superior a 3 horas.
- Rechazo de reservas con duración inferior a 1 hora.
- Validación de que la hora final sea posterior a la inicial.
- Aceptación de reservas de exactamente 1 hora.
- Rechazo de reservas sobre canchas inactivas.
- Detección de solapamientos.
- Creación correcta de una reserva válida.
- Cálculo automático del precio.
- Cálculo de precios para duraciones fraccionadas, como 1 hora y 30 minutos.

---

## Pruebas de rendimiento

El proyecto incluye **pruebas unitarias de rendimiento** mediante `assertTimeout` de JUnit.

Actualmente se evalúan los siguientes escenarios:

| Operación | Criterio |
| --- | ---: |
| Crear una reserva | Menos de 500 ms |
| Calcular el precio de una reserva | Menos de 100 ms |
| Listar 10.000 reservas | Menos de 500 ms |

Estas pruebas se ejecutan con dependencias simuladas mediante **Mockito**, por lo que están orientadas a evaluar el comportamiento del servicio de manera aislada y no el rendimiento completo de PostgreSQL o de la aplicación desplegada. Los umbrales tienen margen para que sean estables en los runners de GitHub Actions.

---

## Pruebas de aceptación con Karate

Las pruebas de aceptación de la API REST están escritas con **Karate** y se ejecutan en una tarea separada (`karateTest`). La aplicación se levanta en un puerto aleatorio con base de datos H2.

| Archivo | Cobertura funcional |
| --- | --- |
| `src/test/resources/karate/usuarios.feature` | Registro, consulta, correo duplicado, validaciones y usuario inexistente (404) |
| `src/test/resources/karate/canchas.feature` | Registro, consulta, cambio de estado, validaciones y cancha inexistente (404) |
| `src/test/resources/karate/reservas.feature` | Creación, cálculo de precio, solapamiento, cancha inactiva, horarios y fechas inválidas, cancelación y recursos inexistentes |

El reporte se genera en `build/karate-reports/karate-summary.html`.

---

## Cobertura de código

El proyecto utiliza **JaCoCo** para medir la cobertura de las pruebas unitarias y de las pruebas Karate en un único reporte (HTML y XML).

El build exige como mínimo **80 % de cobertura de líneas y de ramas**, el mismo umbral del Quality Gate *Sonar way*. Si no se cumple, la tarea `check` falla.

El reporte HTML generado puede consultarse en:

```text
build/reports/jacoco/test/html/index.html
```

---

## Interfaz web

Las plantillas Thymeleaf de `src/main/resources/templates` siguen dos reglas que SonarQube verifica:

- **Accesibilidad:** cada campo de formulario tiene un `id` y su `<label>` lo referencia con `for`, para que los lectores de pantalla anuncien qué dato se pide.
- **Integridad del CDN:** Bootstrap se carga desde jsDelivr con los atributos `integrity` y `crossorigin`. El navegador rechaza el archivo si su contenido no coincide con el hash. Si se cambia la versión de Bootstrap, hay que actualizar el hash con el que publica la [documentación de Bootstrap](https://getbootstrap.com/docs/5.3/getting-started/download/#cdn-via-jsdelivr).

---

## Integración continua y análisis estático

El workflow `.github/workflows/main.yml` se ejecuta en cada push a `main` y en cada Pull Request:

1. Compila el proyecto con Java 17.
2. Ejecuta las pruebas unitarias, las de rendimiento y las de aceptación con Karate.
3. Genera el reporte de cobertura JaCoCo y verifica el umbral mínimo.
4. Envía el análisis estático a **SonarQube Cloud** (proyecto `PleazeTryAgain_court_reserve`, organización `pleazetryagain`) junto con la cobertura y los resultados de pruebas. Se analizan el código Java y las plantillas Thymeleaf.
5. Espera el resultado del **Quality Gate**: si no se cumple, el workflow falla.
6. Publica los reportes de pruebas y el `.jar` como artefactos.

El análisis requiere el secret `SONAR_TOKEN` en el repositorio y tener desactivado el *Automatic Analysis* en SonarQube Cloud.

### Quality Gate

El proyecto usa el Quality Gate **Sonar way**, que se evalúa sobre el código nuevo de cada Pull Request:

| Condición | Umbral |
| --- | --- |
| Cobertura | ≥ 80 % |
| Líneas duplicadas | ≤ 3 % |
| Calificación de confiabilidad, seguridad y mantenibilidad | A |
| Security hotspots revisados | 100 % |

Para que un Pull Request no pueda integrarse con el Quality Gate en rojo, la rama `main` debe estar protegida en GitHub (*Settings → Branches → Add branch ruleset*) con estas reglas:

- Exigir un Pull Request antes de integrar.
- Exigir que el check `build` del workflow termine correctamente.

---

## Ejecución del proyecto

### Requisitos

Antes de ejecutar Court Reserve se requiere:

- Java 17.
- PostgreSQL.
- Git.

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

- Separación de responsabilidades.
- Arquitectura por capas.
- DTOs para solicitudes y respuestas.
- Validación de reglas de negocio.
- Manejo centralizado de excepciones.
- Pruebas unitarias aisladas.
- Mocking de dependencias.
- Patrón AAA.
- Pruebas de rendimiento.
- Cobertura mediante JaCoCo.
- Automatización de construcción y pruebas con Gradle.

---

## Autor

**Andrés Alejandro Villota Villota**

Estudiante de Ingeniería de Sistemas — Universidad de Antioquia

- GitHub: [AndresVillotaVillota](https://github.com/AndresVillotaVillota)
- Correo: [andres.villotav@udea.edu.co](mailto:andres.villotav@udea.edu.co)
