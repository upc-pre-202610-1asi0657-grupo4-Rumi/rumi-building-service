package com.rumi.buildingmanagement.infrastructure.web;

/**
 * Example payloads shown in the OpenAPI documentation.
 */
final class OpenApiExamples {

    static final String PROBLEM_JSON = "application/problem+json";

    static final String BUILDING_REQUEST = """
            {
              "name": "Torre Miraflores",
              "address": "Av. Larco 1234, Miraflores",
              "floors": 12,
              "constructionYear": 2015,
              "units": 48,
              "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            }""";

    static final String BUILDING_RESPONSE = """
            {
              "id": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "name": "Torre Miraflores",
              "address": "Av. Larco 1234, Miraflores",
              "floors": 12,
              "constructionYear": 2015,
              "units": 48,
              "status": "PENDING_SENSORS",
              "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            }""";

    static final String BUILDING_LIST_RESPONSE = """
            [
              {
                "id": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
                "name": "Torre Miraflores",
                "address": "Av. Larco 1234, Miraflores",
                "floors": 12,
                "constructionYear": 2015,
                "units": 48,
                "status": "ACTIVE",
                "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
              }
            ]""";

    static final String INVALID_ADMINISTRATOR_ID_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Failed to convert 'administratorUserId' with value: 'not-a-uuid'",
              "instance": "/api/v1/buildings"
            }""";

    static final String BUILDING_NOT_FOUND_ERROR = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Building 7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d was not found",
              "instance": "/api/v1/buildings/7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d"
            }""";

    static final String INVALID_BUILDING_ID_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Failed to convert 'buildingId' with value: 'not-a-uuid'",
              "instance": "/api/v1/buildings/not-a-uuid"
            }""";

    static final String BUILDING_VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Invalid request content.",
              "instance": "/api/v1/buildings",
              "errors": {
                "floors": "must be greater than or equal to 1",
                "name": "must not be blank"
              }
            }""";

    static final String BUILDING_MALFORMED_BODY = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Failed to read request",
              "instance": "/api/v1/buildings"
            }""";

    static final String SENSOR_REQUEST = """
            {
              "zone": "FLOOR-3-NORTH",
              "type": "ACCELEROMETER"
            }""";

    static final String SENSOR_RESPONSE = """
            {
              "id": "5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11",
              "buildingId": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "zone": "FLOOR-3-NORTH",
              "type": "ACCELEROMETER",
              "status": "PENDING"
            }""";

    static final String SENSOR_VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Invalid request content.",
              "instance": "/api/v1/buildings/7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d/sensors",
              "errors": {
                "zone": "must not be blank"
              }
            }""";

    static final String SENSOR_BUILDING_NOT_FOUND_ERROR = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Building 7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d was not found",
              "instance": "/api/v1/buildings/7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d/sensors"
            }""";

    private OpenApiExamples() {
    }
}
