Feature: Gestión de canchas por la API REST

  Background:
    * url baseUrl

  Scenario: Registrar, consultar, listar y cambiar el estado de una cancha
    Given path 'api/canchas'
    And request { nombre: 'Cancha Karate', tipo: 'TENIS', precioHora: 50000 }
    When method post
    Then status 201
    And match response == { id: '#number', nombre: 'Cancha Karate', tipo: 'TENIS', precioHora: 50000, activa: true }
    * def canchaId = response.id

    Given path 'api/canchas', canchaId
    When method get
    Then status 200
    And match response.nombre == 'Cancha Karate'

    Given path 'api/canchas'
    When method get
    Then status 200
    And match response[*].id contains canchaId

    Given path 'api/canchas', canchaId, 'estado'
    And param activa = false
    When method patch
    Then status 200
    And match response.activa == false

    Given path 'api/canchas', canchaId, 'estado'
    And param activa = true
    When method patch
    Then status 200
    And match response.activa == true

  Scenario Outline: Rechazar datos de cancha inválidos (<caso>)
    Given path 'api/canchas'
    And request { nombre: '<nombre>', tipo: '<tipo>', precioHora: <precio> }
    When method post
    Then status 400
    And match response.<campo> == '<mensaje>'

    Examples:
      | caso              | nombre        | tipo   | precio | campo      | mensaje                          |
      | nombre vacío      |               | FUTBOL | 50000  | nombre     | El nombre es obligatorio         |
      | precio en cero    | Cancha Cero   | FUTBOL | 0      | precioHora | El precio debe ser mayor que cero |
      | precio negativo   | Cancha Menos  | TENIS  | -100   | precioHora | El precio debe ser mayor que cero |

  Scenario: Rechazar una cancha sin tipo
    Given path 'api/canchas'
    And request { nombre: 'Cancha sin tipo', precioHora: 50000 }
    When method post
    Then status 400
    And match response.tipo == 'El tipo de cancha es obligatorio'

  Scenario: Consultar una cancha inexistente
    Given path 'api/canchas', idInexistente
    When method get
    Then status 404
    And match response.error == 'Cancha no encontrada'

  Scenario: Cambiar el estado de una cancha inexistente
    Given path 'api/canchas', idInexistente, 'estado'
    And param activa = false
    When method patch
    Then status 404
    And match response.error == 'Cancha no encontrada'
