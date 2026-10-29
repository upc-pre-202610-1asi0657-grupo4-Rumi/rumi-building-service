Feature: Building management backed by PostgreSQL
  The Building service stores buildings, resident invitations and sensors in PostgreSQL.

  Scenario: Register a building and persist it with pending sensor status
    When I register a building named "Torre de Prueba" for administrator "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
    Then the building response has status 201 and state "PENDING_SENSORS"
    And the registered building can be retrieved from the service

  Scenario: Generate and retrieve a resident invitation for a building
    Given I have registered a building named "Edificio Invitaciones"
    When I generate a resident invitation for that building
    Then the invitation response has status 201 and state "PENDING"
    And the invitation is listed for that building

  Scenario: Register a sensor in a building zone
    Given I have registered a building named "Edificio Sensores"
    When I register an "ACCELEROMETER" sensor in zone "FLOOR-3-NORTH"
    Then the sensor response has status 201 and state "PENDING"
    And the sensor is listed for that building
