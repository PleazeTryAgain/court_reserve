Feature: Court Reserve REST API acceptance tests

  Background:
    * url baseUrl

  Scenario: Manage a user, court, and reservation through the public API
    Given path 'api/usuarios'
    And request { nombre: 'Karate Acceptance User', correo: '#(uniqueEmail)', telefono: '3001234567' }
    When method post
    Then status 201
    And match response.id == '#number'
    And match response.nombre == 'Karate Acceptance User'
    And match response.correo == uniqueEmail
    * def usuarioId = response.id

    Given path 'api/usuarios'
    When method get
    Then status 200
    And match response[*].id contains usuarioId

    Given path 'api/usuarios', usuarioId
    When method get
    Then status 200
    And match response.id == usuarioId

    Given path 'api/usuarios'
    And request { nombre: 'Duplicate Email', correo: '#(uniqueEmail)', telefono: '3000000000' }
    When method post
    Then status 400

    Given path 'api/canchas'
    And request { nombre: 'Karate Court', tipo: 'TENIS', precioHora: 50000 }
    When method post
    Then status 201
    And match response.id == '#number'
    And match response.activa == true
    And match response.precioHora == 50000
    * def canchaId = response.id

    Given path 'api/canchas', canchaId
    When method get
    Then status 200
    And match response.nombre == 'Karate Court'

    Given path 'api/canchas', canchaId, 'estado'
    And param activa = false
    When method patch
    Then status 200
    And match response.activa == false

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '11:00:00' }
    When method post
    Then status 409
    And match response.error == 'La cancha no está disponible'

    Given path 'api/canchas', canchaId, 'estado'
    And param activa = true
    When method patch
    Then status 200
    And match response.activa == true

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '12:00:00' }
    When method post
    Then status 201
    And match response.estado == 'ACTIVA'
    And match response.precioTotal == 100000
    And match response.usuarioId == usuarioId
    And match response.canchaId == canchaId
    * def reservaId = response.id

    Given path 'api/reservas'
    And request { usuarioId: '#(usuarioId)', canchaId: '#(canchaId)', fecha: '#(futureDate)', horaInicio: '11:00:00', horaFin: '12:00:00' }
    When method post
    Then status 409
    And match response.error == 'La cancha ya está reservada en ese horario'

    Given path 'api/reservas', reservaId, 'cancelar'
    When method patch
    Then status 200
    And match response.estado == 'CANCELADA'

    Given path 'api/reservas'
    When method get
    Then status 200
    And match response[*].id contains reservaId

  Scenario: Reject malformed user and invalid reservation time ranges
    Given path 'api/usuarios'
    And request { nombre: '', correo: 'not-an-email', telefono: '300' }
    When method post
    Then status 400
    And match response.nombre == 'El nombre es obligatorio'
    And match response.correo == 'El correo debe tener un formato válido'

    Given path 'api/reservas'
    And request { usuarioId: 999999, canchaId: 999999, fecha: '#(futureDate)', horaInicio: '10:00:00', horaFin: '10:30:00' }
    When method post
    Then status 400
    And match response.error == 'La reserva debe durar al menos 1 hora'
