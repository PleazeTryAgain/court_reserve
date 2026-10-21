Feature: Gestión de reservas por la API REST

  Background:
    * url baseUrl

    Given path 'api/usuarios'
    And request { nombre: 'Cliente Reservas', correo: '#(uniqueEmail)', telefono: '3001234567' }
    When method post
    Then status 201
    * def usuarioId = response.id

    Given path 'api/canchas'
    And request { nombre: 'Cancha Reservas', tipo: 'FUTBOL', precioHora: 50000 }
    When method post
    Then status 201
    * def canchaId = response.id

    * def reserva = { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '12:00:00' }

  Scenario: Crear, listar y cancelar una reserva
    Given path 'api/reservas'
    And request reserva
    When method post
    Then status 201
    And match response.id == '#number'
    And match response.estado == 'ACTIVA'
    And match response.precioTotal == 100000
    And match response.usuarioId == usuarioId
    And match response.canchaId == canchaId
    And match response.usuarioNombre == 'Cliente Reservas'
    And match response.canchaNombre == 'Cancha Reservas'
    * def reservaId = response.id

    Given path 'api/reservas'
    When method get
    Then status 200
    And match response[*].id contains reservaId

    Given path 'api/reservas', reservaId, 'cancelar'
    When method patch
    Then status 200
    And match response.estado == 'CANCELADA'

    Given path 'api/reservas', reservaId, 'cancelar'
    When method patch
    Then status 409
    And match response.error == 'La reserva ya está cancelada'

  Scenario: Calcular el precio de una reserva por fracción de hora
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '14:00:00', horaFin: '15:30:00' }
    When method post
    Then status 201
    And match response.precioTotal == 75000

  Scenario: Rechazar una reserva que se solapa con otra activa
    Given path 'api/reservas'
    And request reserva
    When method post
    Then status 201

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '11:00:00', horaFin: '12:00:00' }
    When method post
    Then status 409
    And match response.error == 'La cancha ya está reservada en ese horario'

  Scenario: Permitir reservar de nuevo un horario cancelado
    Given path 'api/reservas'
    And request reserva
    When method post
    Then status 201
    * def reservaId = response.id

    Given path 'api/reservas', reservaId, 'cancelar'
    When method patch
    Then status 200

    Given path 'api/reservas'
    And request reserva
    When method post
    Then status 201

  Scenario: Rechazar una reserva en una cancha inactiva
    Given path 'api/canchas', canchaId, 'estado'
    And param activa = false
    When method patch
    Then status 200

    Given path 'api/reservas'
    And request reserva
    When method post
    Then status 409
    And match response.error == 'La cancha no está disponible'

  Scenario Outline: Rechazar horarios inválidos (<caso>)
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '<inicio>', horaFin: '<fin>' }
    When method post
    Then status 400
    And match response.error == '<mensaje>'

    Examples:
      | caso                | inicio   | fin      | mensaje                                           |
      | fin antes de inicio | 12:00:00 | 10:00:00 | La hora final debe ser posterior a la hora inicial |
      | misma hora          | 10:00:00 | 10:00:00 | La hora final debe ser posterior a la hora inicial |
      | menos de una hora   | 10:00:00 | 10:30:00 | La reserva debe durar al menos 1 hora              |
      | más de tres horas   | 08:00:00 | 11:30:00 | La reserva no puede superar las 3 horas            |

  Scenario: Rechazar una reserva en una fecha pasada
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(pastDate)', horaInicio: '10:00:00', horaFin: '11:00:00' }
    When method post
    Then status 400
    And match response.error == 'No se puede reservar en una fecha pasada'

  Scenario: Rechazar una reserva sin datos obligatorios
    Given path 'api/reservas'
    And request {}
    When method post
    Then status 400
    And match response.usuarioId == 'El usuario es obligatorio'
    And match response.canchaId == 'La cancha es obligatoria'
    And match response.fecha == 'La fecha es obligatoria'
    And match response.horaInicio == 'La hora inicial es obligatoria'
    And match response.horaFin == 'La hora final es obligatoria'

  Scenario: Rechazar una reserva para un usuario inexistente
    Given path 'api/reservas'
    And request { usuarioId: '#(idInexistente)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '11:00:00' }
    When method post
    Then status 404
    And match response.error == 'Usuario no encontrado'

  Scenario: Rechazar una reserva para una cancha inexistente
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(idInexistente)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '11:00:00' }
    When method post
    Then status 404
    And match response.error == 'Cancha no encontrada'

  Scenario: Cancelar una reserva inexistente
    Given path 'api/reservas', idInexistente, 'cancelar'
    When method patch
    Then status 404
    And match response.error == 'Reserva no encontrada'

  Scenario: Permitir reservas consecutivas sin solapamiento
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '11:00:00' }
    When method post
    Then status 201
    And match response.precioTotal == 50000

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '11:00:00', horaFin: '12:00:00' }
    When method post
    Then status 201
    And match response.estado == 'ACTIVA'
    And match response.precioTotal == 50000

  Scenario: Rechazar una reserva que contiene por completo otra reserva activa
    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '11:00:00', horaFin: '12:00:00' }
    When method post
    Then status 201

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '13:00:00' }
    When method post
    Then status 409
    And match response.error == 'La cancha ya está reservada en ese horario'
