# Pruebas de aceptación — Court Reserve

## Característica: Gestión de canchas mediante la API REST

**Como** persona usuaria del sistema  
**Quiero** registrar, consultar y cambiar el estado de las canchas  
**Para** mantener la información de las instalaciones y su disponibilidad.

### Escenario: Registrar, consultar, listar y cambiar el estado de una cancha
- **Dado** que la API está disponible
- **Cuando** registro una cancha con nombre, tipo y precio por hora válidos
- **Entonces** la API responde con código `201` y la cancha queda activa
- **Y** puedo consultar la cancha por su identificador
- **Y** puedo encontrarla en el listado de canchas
- **Y** puedo desactivarla y volver a activarla

### Esquema del escenario: Rechazar datos de cancha inválidos
- **Dado** que envío una cancha con datos inválidos: **<caso>**
- **Cuando** intento registrarla
- **Entonces** la API responde con código `400`
- **Y** informa el error de validación correspondiente

| caso | resultado esperado |
|---|---|
| Nombre vacío | Se indica que el nombre es obligatorio |
| Precio igual a cero | Se indica que el precio debe ser mayor que cero |
| Precio negativo | Se indica que el precio debe ser mayor que cero |
| Tipo de cancha ausente | Se indica que el tipo de cancha es obligatorio |
| Precio por hora ausente | Se indica que el precio por hora es obligatorio |
| Nombre de más de 100 caracteres | Se indica que el nombre no puede superar los 100 caracteres |

### Escenario: Consultar una cancha inexistente
- **Dado** que no existe una cancha con el identificador solicitado
- **Cuando** consulto esa cancha
- **Entonces** la API responde con código `404`
- **Y** informa que la cancha no fue encontrada

### Escenario: Cambiar el estado de una cancha inexistente
- **Dado** que no existe una cancha con el identificador solicitado
- **Cuando** intento cambiar su estado
- **Entonces** la API responde con código `404`
- **Y** informa que la cancha no fue encontrada

---

## Característica: Gestión de usuarios mediante la API REST

**Como** persona usuaria del sistema  
**Quiero** registrar, consultar y listar usuarios  
**Para** asociarlos con sus reservas.

### Escenario: Registrar, listar y consultar un usuario
- **Dado** que la API está disponible
- **Cuando** registro un usuario con nombre, correo electrónico y teléfono válidos
- **Entonces** la API responde con código `201`
- **Y** devuelve los datos del usuario registrado
- **Y** puedo encontrarlo en el listado de usuarios
- **Y** puedo consultarlo por su identificador

### Escenario: Rechazar un correo electrónico duplicado
- **Dado** que ya existe un usuario con un correo electrónico determinado
- **Cuando** intento registrar otro usuario con el mismo correo
- **Entonces** la API responde con código `400`
- **Y** informa que ya existe un usuario con ese correo

### Escenario: Rechazar datos de usuario inválidos
- **Dado** que envío un usuario con el nombre vacío, un correo con formato inválido o un teléfono de más de 20 caracteres
- **Cuando** intento registrarlo
- **Entonces** la API responde con código `400`
- **Y** informa los errores de validación correspondientes

### Escenario: Rechazar un usuario sin correo electrónico
- **Dado** que envío un usuario sin correo electrónico
- **Cuando** intento registrarlo
- **Entonces** la API responde con código `400`
- **Y** indica que el correo es obligatorio

### Escenario: Rechazar un nombre de usuario de más de 100 caracteres
- **Dado** que envío un nombre que supera los 100 caracteres
- **Cuando** intento registrar el usuario
- **Entonces** la API responde con código `400`
- **Y** indica que el nombre no puede superar los 100 caracteres

### Escenario: Consultar un usuario inexistente
- **Dado** que no existe un usuario con el identificador solicitado
- **Cuando** consulto ese usuario
- **Entonces** la API responde con código `404`
- **Y** informa que el usuario no fue encontrado

---

## Característica: Gestión de reservas mediante la API REST

**Como** persona usuaria del sistema  
**Quiero** crear, consultar y cancelar reservas  
**Para** administrar el uso de las canchas y calcular sus precios.

**Antecedentes:**
- **Dado** que existe un usuario válido
- **Y** que existe una cancha activa con un precio de 50 000 por hora
- **Y** que la fecha de la reserva es futura, salvo que el escenario indique lo contrario

### Escenario: Crear, listar y cancelar una reserva
- **Cuando** creo una reserva de dos horas para el usuario y la cancha existentes
- **Entonces** la API responde con código `201`
- **Y** la reserva queda en estado `ACTIVA`
- **Y** el precio total es 100 000
- **Y** la respuesta incluye los identificadores y nombres del usuario y de la cancha
- **Cuando** consulto el listado de reservas
- **Entonces** encuentro la reserva creada
- **Cuando** cancelo esa reserva
- **Entonces** la API responde con código `200` y el estado pasa a `CANCELADA`

### Escenario: Rechazar la cancelación repetida de una reserva
- **Dado** que una reserva ya está cancelada
- **Cuando** intento cancelarla nuevamente
- **Entonces** la API responde con código `409`
- **Y** informa que la reserva ya está cancelada

### Escenario: Calcular el precio proporcional a una fracción de hora
- **Dado** que la cancha cuesta 50 000 por hora
- **Cuando** creo una reserva de una hora y media
- **Entonces** la API responde con código `201`
- **Y** el precio total es 75 000

### Escenario: Rechazar una reserva que se solapa con otra reserva activa
- **Dado** que existe una reserva activa de 10:00 a 12:00 para una cancha y una fecha determinadas
- **Cuando** intento reservar esa misma cancha de 11:00 a 12:00 en la misma fecha
- **Entonces** la API responde con código `409`
- **Y** informa que la cancha ya está reservada en ese horario

### Escenario: Permitir reservar nuevamente un horario cancelado
- **Dado** que existe una reserva para un horario determinado
- **Cuando** cancelo la reserva
- **Y** intento reservar nuevamente el mismo horario
- **Entonces** la nueva reserva se crea con código `201`

### Escenario: Rechazar una reserva en una cancha inactiva
- **Dado** que la cancha está desactivada
- **Cuando** intento reservarla
- **Entonces** la API responde con código `409`
- **Y** informa que la cancha no está disponible

### Esquema del escenario: Rechazar duraciones u horarios inválidos
- **Dado** que solicito una reserva con el siguiente horario: **<caso>**
- **Cuando** envío la solicitud
- **Entonces** la API responde con código `400`
- **Y** informa que **<resultado esperado>**

| caso | resultado esperado |
|---|---|
| La hora final es anterior a la inicial | La hora final debe ser posterior a la hora inicial |
| La hora final coincide con la inicial | La hora final debe ser posterior a la hora inicial |
| La duración es menor de una hora | La reserva debe durar al menos 1 hora |
| La duración supera las tres horas | La reserva no puede superar las 3 horas |

### Escenario: Rechazar una reserva en una fecha pasada
- **Dado** que la fecha solicitada ya pasó
- **Cuando** intento crear la reserva
- **Entonces** la API responde con código `400`
- **Y** informa que no se puede reservar en una fecha pasada

### Escenario: Rechazar una reserva sin datos obligatorios
- **Dado** que envío una solicitud de reserva vacía
- **Cuando** intento crear la reserva
- **Entonces** la API responde con código `400`
- **Y** indica que el usuario, la cancha, la fecha, la hora inicial y la hora final son obligatorios

### Escenario: Rechazar una reserva para un usuario inexistente
- **Dado** que el identificador del usuario no corresponde a ningún usuario registrado
- **Cuando** intento crear una reserva con ese identificador
- **Entonces** la API responde con código `404`
- **Y** informa que el usuario no fue encontrado

### Escenario: Rechazar una reserva para una cancha inexistente
- **Dado** que el identificador de la cancha no corresponde a ninguna cancha registrada
- **Cuando** intento crear una reserva con ese identificador
- **Entonces** la API responde con código `404`
- **Y** informa que la cancha no fue encontrada

### Escenario: Cancelar una reserva inexistente
- **Dado** que no existe una reserva con el identificador solicitado
- **Cuando** intento cancelarla
- **Entonces** la API responde con código `404`
- **Y** informa que la reserva no fue encontrada

### Escenario: Permitir reservas consecutivas sin solapamiento
- **Dado** que una cancha está disponible de 10:00 a 12:00 en una fecha determinada
- **Cuando** creo una reserva de 10:00 a 11:00
- **Y** creo otra reserva de 11:00 a 12:00 para la misma cancha y fecha
- **Entonces** ambas reservas se crean con código `201`
- **Y** cada reserva tiene un precio total de 50 000

### Escenario: Rechazar una reserva que contiene por completo otra reserva activa
- **Dado** que existe una reserva activa de 11:00 a 12:00 para una cancha y una fecha determinadas
- **Cuando** intento reservar esa misma cancha de 10:00 a 13:00 en la misma fecha
- **Entonces** la API responde con código `409`
- **Y** informa que la cancha ya está reservada en ese horario

---

## Característica: Ejecución de las pruebas de aceptación

**Como** equipo de desarrollo  
**Quiero** ejecutar las pruebas de aceptación contra la API  
**Para** verificar sus respuestas y reglas de negocio automáticamente.

### Escenario: Ejecutar la suite de aceptación
- **Dado** que el proyecto tiene configurado Karate con JUnit 5
- **Y** que las pruebas usan el perfil de pruebas y una base de datos H2
- **Cuando** ejecuto `./gradlew karateTest`
- **Entonces** se ejecutan las características de Karate ubicadas en `src/test/resources/karate`
- **Y** la aplicación de prueba se inicia automáticamente en un puerto aleatorio
- **Y** no necesito iniciar la aplicación por separado

**Implementación:** el ejecutor se encuentra en `src/test/java/com/courtreserve/acceptance/AcceptanceTest.java`. La configuración común, incluida la URL base y los datos de prueba, se define en `src/test/resources/karate-config.js`.
