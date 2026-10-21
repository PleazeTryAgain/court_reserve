Feature: Gestión de usuarios por la API REST

  Background:
    * url baseUrl
    * def usuario = { nombre: 'Usuario Karate', correo: '#(uniqueEmail)', telefono: '3001234567' }

  Scenario: Registrar, listar y consultar un usuario
    Given path 'api/usuarios'
    And request usuario
    When method post
    Then status 201
    And match response == { id: '#number', nombre: 'Usuario Karate', correo: '#(uniqueEmail)', telefono: '3001234567' }
    * def usuarioId = response.id

    Given path 'api/usuarios'
    When method get
    Then status 200
    And match response == '#[_ > 0]'
    And match response[*].id contains usuarioId

    Given path 'api/usuarios', usuarioId
    When method get
    Then status 200
    And match response.id == usuarioId
    And match response.correo == uniqueEmail

  Scenario: Rechazar un correo duplicado
    Given path 'api/usuarios'
    And request usuario
    When method post
    Then status 201

    Given path 'api/usuarios'
    And request { nombre: 'Otro usuario', correo: '#(uniqueEmail)', telefono: '3000000000' }
    When method post
    Then status 400
    And match response.error == 'Ya existe un usuario con ese correo'

  Scenario: Rechazar datos de usuario inválidos
    Given path 'api/usuarios'
    And request { nombre: '', correo: 'no-es-un-correo', telefono: '300123456789012345678' }
    When method post
    Then status 400
    And match response.nombre == 'El nombre es obligatorio'
    And match response.correo == 'El correo debe tener un formato válido'
    And match response.telefono == 'El teléfono no puede superar los 20 caracteres'

  Scenario: Consultar un usuario inexistente
    Given path 'api/usuarios', idInexistente
    When method get
    Then status 404
    And match response.error == 'Usuario no encontrado'

  Scenario: Rechazar un usuario sin correo
    Given path 'api/usuarios'
    And request { nombre: 'Usuario sin correo', telefono: '3001234567' }
    When method post
    Then status 400
    And match response.correo == 'El correo es obligatorio'

  Scenario: Rechazar un nombre de usuario que supera el límite
    Given path 'api/usuarios'
    And request { nombre: '#(nombreUsuarioLargo)', correo: '#(uniqueEmail)', telefono: '3001234567' }
    When method post
    Then status 400
    And match response.nombre == 'El nombre no puede superar los 100 caracteres'
